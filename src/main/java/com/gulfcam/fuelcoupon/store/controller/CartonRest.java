package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.store.dto.CreateCartonDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    IStatusRepo iStatusRepo;

    @Autowired
    ICartonRepo iCartonRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un Carton", tags = "Carton", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addCarton(@Valid @RequestBody CreateCartonDTO createCartonDTO) {

        Users storeKeeper = new Users();
        Storehouse storehouse = new Storehouse();

        if (createCartonDTO.getIdStoreHouse()  != null) {
            if(!iStorehouseService.getByInternalReference(createCartonDTO.getIdStoreHouse()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse = iStorehouseService.getByInternalReference(createCartonDTO.getIdStoreHouse()).get();

        }

        if (createCartonDTO.getIdStoreKeeper() != null) {
            storeKeeper = iUserService.getByInternalReference(createCartonDTO.getIdStoreKeeper());
            if(storeKeeper.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        Carton carton = new Carton();
        carton.setInternalReference(jwtUtils.generateInternalReference());
        carton.setCreatedAt(LocalDateTime.now());
        carton.setFrom(createCartonDTO.getFrom());
        carton.setSerialFrom(createCartonDTO.getSerialFrom());
        carton.setSerialTo(createCartonDTO.getSerialTo());
        carton.setNumber(createCartonDTO.getNumber());
        carton.setTo(createCartonDTO.getTo());
        carton.setTypeVoucher(createCartonDTO.getTypeVoucher());
        carton.setIdStoreHouse(createCartonDTO.getIdStoreHouse());
        carton.setIdStoreKeeper(createCartonDTO.getIdStoreKeeper());

        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        carton.setStatus(status);


        Map<String, Object> CartonEncoded = new HashMap<>();
        CartonEncoded = iCartonService.createCarton(carton);
        carton = (Carton) CartonEncoded.get("carton");
        generateCoupon(carton);
        return ResponseEntity.ok(carton);
    }

    private void generateCoupon(Carton carton){

        int numberCouponByCarton = 1 + (carton.getFrom()-carton.getTo())/10;
        for(int i=carton.getTo(); i< carton.getTo() + 10000; i++){
            Notebook notebook = new Notebook();
            notebook.setInternalReference(jwtUtils.generateInternalReference());
            notebook.setCreatedAt(LocalDateTime.now());
            notebook.setSerialNumber("DE "+carton.getTypeVoucher()+"-"+carton.getSerialTo()+"  "+"A "+carton.getTypeVoucher()+"-"+carton.getSerialFrom()+" ");
            notebook.setIdCarton(carton.getInternalReference());
            notebook.setIdStoreKeeper(carton.getIdStoreKeeper());
            Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
            notebook.setStatus(status);

            Map<String, Object> notebookEncoded = new HashMap<>();
            notebookEncoded = iNotebookService.createNotebook(notebook);
            notebook = (Notebook) notebookEncoded.get("notebook");
            for(int y=0; y<10; y++){
                int x = 0;
                x = x + i;
                Coupon coupon = new Coupon();
                Long ref = jwtUtils.generateInternalReference();
                if (!iCouponService.existsCouponByInternalReference(ref)){
                    coupon.setInternalReference(ref);
                }else {
                    Long ref2 = jwtUtils.generateInternalReference();
                    coupon.setInternalReference(ref2);
                }
                coupon.setCreatedAt(LocalDateTime.now());
                coupon.setIdNotebook(notebook.getInternalReference());
                coupon.setSerialNumber(x+"");

                coupon.setIdNotebook(notebook.getInternalReference());
                Status statusCoupon = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
                coupon.setStatus(statusCoupon);
                iCouponService.createCoupon(coupon);i++;
            }i--;


        }
    }


    @Operation(summary = "Modification des informations pour un Carton", tags = "Carton", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateCarton(@Valid @RequestBody CreateCartonDTO createCartonDTO, @PathVariable Long internalReference) {

        Users storeKeeper = new Users();
        Storehouse storehouse = new Storehouse();
        if (!iCartonService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.carton_exists", null, LocaleContextHolder.getLocale())));
        }
        Carton carton = iCartonService.getByInternalReference(internalReference).get();

        if (createCartonDTO.getIdStoreHouse()  != null) {
            if(!iStorehouseService.getByInternalReference(createCartonDTO.getIdStoreHouse()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse = iStorehouseService.getByInternalReference(createCartonDTO.getIdStoreHouse()).get();

        }

        if (createCartonDTO.getIdStoreKeeper() != null) {
            storeKeeper = iUserService.getByInternalReference(createCartonDTO.getIdStoreKeeper());
            if(storeKeeper.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        carton.setUpdateAt(LocalDateTime.now());
        carton.setFrom(createCartonDTO.getFrom());
        carton.setSerialFrom(createCartonDTO.getSerialFrom());
        carton.setSerialTo(createCartonDTO.getSerialTo());
        carton.setNumber(createCartonDTO.getNumber());
        carton.setTo(createCartonDTO.getTo());
        carton.setTypeVoucher(createCartonDTO.getTypeVoucher());
        if(createCartonDTO.getIdStoreHouse() != null)
            carton.setIdStoreHouse(createCartonDTO.getIdStoreHouse());
        if(createCartonDTO.getIdStoreKeeper() != null)
            carton.setIdStoreKeeper(createCartonDTO.getIdStoreKeeper());

        iCartonService.createCarton(carton);

        return ResponseEntity.ok(carton);
    }

    @Operation(summary = "Recupérer la liste des Cartons par Magasinier", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/storekeeper/{idStoreKeeper:[0-9]+}")
    public ResponseEntity<Page<Carton>> getCartonsByIdStoreKeeper(@PathVariable Long idStoreKeeper,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idStoreKeeper") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Carton> cartons = iCartonService.getCartonsByIdStoreKeeper(idStoreKeeper,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(cartons);
    }

    @Operation(summary = "Recupérer la liste des Cartons par Entrepôt", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/storehouse/{idStoreHouse:[0-9]+}")
    public ResponseEntity<Page<Carton>> getCartonsByIdStoreHouse(@PathVariable Long idStoreHouse,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idStoreHouse") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Carton> cartons = iCartonService.getCartonsByIdStoreHouse(idStoreHouse,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(cartons);
    }

    @Operation(summary = "Recupérer Un Carton par sa reference interne", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Carton.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Carton> getCartonByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iCartonService.getByInternalReference(internalReference).get());
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
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Carton> list = iCartonService.getAllCartons(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
