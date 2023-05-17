package com.gulfcam.fuelcoupon.store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.dto.CreateCartonDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCartonDTO;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Notebook;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.service.ICartonService;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
import com.gulfcam.fuelcoupon.store.service.INotebookService;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import com.gulfcam.fuelcoupon.utilities.repository.IStatusRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.maven.lifecycle.internal.LifecycleStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Carton")
@RequestMapping("/api/v1.0/carton")
@Slf4j
public class CartonRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    ICartonService iCartonService;

    @Autowired
    ICouponService iCouponService;

    @Autowired
    INotebookService iNotebookService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    IUserService iUserService;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    ICartonRepo iCartonRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;
    JsonMapper jsonMapper = new JsonMapper();
    AESUtil aes = new AESUtil();
    @Value("${app.key}")
    String key;

    @Operation(summary = "création des informations pour un Carton ou Ordre de stockage", tags = "Carton", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addCarton(@RequestBody CreateCartonDTO createCartonDTO) throws JsonProcessingException {
        log.info("idmanager " + aes.decrypt(key, createCartonDTO.getIdStoreHouseStockage().toString()));
        Users storeKeeper = new Users();
        Storehouse storehouse = new Storehouse();
        List<Carton> cartonList = iCartonService.getCartonsByIdStoreHouse(Long.parseLong(aes.decrypt(key, createCartonDTO.getIdStoreHouseStockage().toString())));

        for (Carton item: cartonList){
            if(item.getNumber() == Integer.parseInt(aes.decrypt(key, createCartonDTO.getNumber()+""))){
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.carton_to_storehouse_exists", null, LocaleContextHolder.getLocale())));
            }

            if(item.getFrom() == Integer.parseInt(aes.decrypt(key, createCartonDTO.getFrom()+"")) || item.getTo() == Integer.parseInt(aes.decrypt(key, createCartonDTO.getTo()+""))){
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.plage_to_storehouse_exists", null, LocaleContextHolder.getLocale())));
            }
        }
        if (Integer.parseInt(aes.decrypt(key, createCartonDTO.getTypeVoucher()+"")) != 0) {
            if(!iTypeVoucherService.getTypeVoucherByAmountEquals(Integer.parseInt(aes.decrypt(key, createCartonDTO.getTypeVoucher() +""))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typeVoucher_exists", null, LocaleContextHolder.getLocale())));

        }
        if (createCartonDTO.getIdStoreHouseStockage() != null) {
            if(!iStorehouseService.getByInternalReference(Long.parseLong(aes.decrypt(key, createCartonDTO.getIdStoreHouseStockage().toString()))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse = iStorehouseService.getByInternalReference(Long.parseLong(aes.decrypt(key, createCartonDTO.getIdStoreHouseStockage().toString()))).get();

        }

        if (createCartonDTO.getIdSpaceManager1() != null) {
            storeKeeper = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createCartonDTO.getIdSpaceManager1().toString())));
            if(storeKeeper.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        Carton carton = new Carton();
        carton.setInternalReference(jwtUtils.generateInternalReference());
        carton.setCreatedAt(LocalDateTime.now());
        carton.setFrom(Integer.parseInt(aes.decrypt(key, createCartonDTO.getFrom()+"")));
        carton.setSerialFrom(Integer.parseInt(aes.decrypt(key, createCartonDTO.getSerialFrom()+"")));
        carton.setSerialTo(Integer.parseInt(aes.decrypt(key, createCartonDTO.getSerialTo()+"")));
        carton.setNumber(Integer.parseInt(aes.decrypt(key, createCartonDTO.getNumber()+"")));
        carton.setTo(Integer.parseInt(aes.decrypt(key, createCartonDTO.getTo()+"")));
        carton.setTypeVoucher(Integer.parseInt(aes.decrypt(key, createCartonDTO.getTypeVoucher()+"")));
        carton.setIdStoreHouse(Long.parseLong(aes.decrypt(key, createCartonDTO.getIdStoreHouseStockage().toString())));
        carton.setIdSpaceManager1(Long.parseLong(aes.decrypt(key, createCartonDTO.getIdSpaceManager1().toString())));

        Status status = iStatusRepo.findByName(EStatus.AVAILABLE).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.AVAILABLE +  "  not found"));
        carton.setStatus(status);


        Map<String, Object> CartonEncoded = new HashMap<>();
        CartonEncoded = iCartonService.createCarton(carton, +1);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(carton);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }


    @Operation(summary = "Ordre d'approvisionnement de l'entrepôt de vente en carnets", tags = "Carton", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/supply")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> orderSupply(@RequestParam("idCarton") String idCarton, @RequestParam("idStoreHouseSell") String idStoreHouseSell) throws JsonProcessingException {

        Users storeKeeper = new Users();
        Storehouse storehouse = new Storehouse();


        if (idStoreHouseSell != null) {
            if(!iStorehouseService.getByInternalReference(Long.parseLong(aes.decrypt(key, idStoreHouseSell))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse = iStorehouseService.getByInternalReference(Long.parseLong(aes.decrypt(key, idStoreHouseSell))).get();

        }

        if (!iCartonService.getByInternalReference(Long.parseLong(aes.decrypt(key, idCarton))).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.carton_exists", null, LocaleContextHolder.getLocale())));
        }
        Carton carton = iCartonService.getByInternalReference(Long.parseLong(aes.decrypt(key, idCarton))).get();

        if (carton.getStatus().getName() == EStatus.DISABLED) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.carton_used", null, LocaleContextHolder.getLocale())));
        }

        carton.setUpdateAt(LocalDateTime.now());

        Status status = iStatusRepo.findByName(EStatus.DISABLED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.DISABLED +  "  not found"));
        carton.setStatus(status);

        Map<String, Object> CartonEncoded = new HashMap<>();
        CartonEncoded = iCartonService.supplyStoreHouse(carton, storehouse);
        carton = (Carton) CartonEncoded.get("carton");
//        generateCoupon(carton);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(carton);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }


    @Operation(summary = "Modification des informations pour un Carton", tags = "Carton", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateCarton(@Valid @RequestBody CreateCartonDTO createCartonDTO, @PathVariable Long internalReference) {

//        Users storeKeeper = new Users();
//        Storehouse storehouse = new Storehouse();
//        if (!iCartonService.getByInternalReference(internalReference).isPresent()) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.carton_exists", null, LocaleContextHolder.getLocale())));
//        }
//        Carton carton = iCartonService.getByInternalReference(internalReference).get();
//
//        if (createCartonDTO.getIdStoreHouseStockage()  != null) {
//            if(!iStorehouseService.getByInternalReference(createCartonDTO.getIdStoreHouseStockage()).isPresent())
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
//            storehouse = iStorehouseService.getByInternalReference(createCartonDTO.getIdStoreHouseStockage()).get();
//
//        }
//
//        if (createCartonDTO.getIdSpaceManager1() != null) {
//            storeKeeper = iUserService.getByInternalReference(createCartonDTO.getIdSpaceManager1());
//            if(storeKeeper.getUserId() == null)
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
//        }
//        carton.setUpdateAt(LocalDateTime.now());
//        carton.setFrom(createCartonDTO.getFrom());
//        carton.setSerialFrom(createCartonDTO.getSerialFrom());
//        carton.setSerialTo(createCartonDTO.getSerialTo());
//        carton.setNumber(createCartonDTO.getNumber());
//        carton.setTo(createCartonDTO.getTo());
//        carton.setTypeVoucher(createCartonDTO.getTypeVoucher());
//        if(createCartonDTO.getIdStoreHouseStockage() != null)
//            carton.setIdStoreHouse(createCartonDTO.getIdStoreHouseStockage());
//        if(createCartonDTO.getIdSpaceManager1() != null)
//            carton.setIdSpaceManager1(createCartonDTO.getIdSpaceManager1());
//
//        iCartonService.createCarton(carton, 0);

//        return ResponseEntity.ok(carton);
        return null;
    }

    @Operation(summary = "Recupérer la liste des Cartons par Gestionnaire espace 1", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/spacemanager/{idSpaceManager1:[0-9]+}")
    public ResponseEntity<Page<Carton>> getCartonsByIdSpaceManager1(@PathVariable Long idSpaceManager1,
                                                                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                  @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                  @RequestParam(required = false, defaultValue = "idSpaceManager1") String sort,
                                                                  @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Carton> cartons = iCartonService.getCartonsByIdSpaceManager1(idSpaceManager1,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(cartons);
    }

    @Operation(summary = "Recupérer la liste des Cartons par Entrepôt", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/storehouse/{idStoreHouse}")
    public ResponseEntity<?> getCartonsByIdStoreHouse(@PathVariable String idStoreHouse,
                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                 @RequestParam(required = false, defaultValue = "idStoreHouse") String sort,
                                                                 @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {

        Page<Carton> cartons = iCartonService.getCartonsByIdStoreHouse(Long.parseLong(aes.decrypt(key, idStoreHouse)),
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(cartons);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Un Carton par sa reference interne", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getCartonByInternalReference(@PathVariable String internalReference) throws JsonProcessingException {
        Carton carton = iCartonService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(carton);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Supprimer un Carton", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", description = "Carton deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteCarton(@PathVariable Long internalReference) {
        Carton carton = iCartonService.getByInternalReference(internalReference).get();
        iCartonService.deleteCarton(carton);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Cartons", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllCartons(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                           @RequestParam(required = false, defaultValue = "id") String sort,
                                           @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCartonDTO> list = iCartonService.getAllCartons(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "filtre des Cartons", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filterCartons(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                           @RequestParam(required = false, value = "number") String number,
                                           @RequestParam(required = false, value = "storehouse") String storeHouse,
                                           @RequestParam(required = false, value = "status") String status,
                                           @RequestParam(required = false, value = "type") String type,
                                           @RequestParam(required = false, value = "spacemanager1") String spacemanager1,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)            @RequestParam(required = false, value = "date" ) LocalDate date,
                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                           @RequestParam(required = false, defaultValue = "id") String sort,
                                           @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCartonDTO> list = iCartonService.filtres(number, status, storeHouse, date, spacemanager1, type, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }
}
