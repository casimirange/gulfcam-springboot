package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.entity.Unit;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.order.service.IUnitService;
import com.gulfcam.fuelcoupon.store.dto.CreateStockMovementDTO;
import com.gulfcam.fuelcoupon.store.entity.StockMovement;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.repository.IStokMovementRepo;
import com.gulfcam.fuelcoupon.store.service.*;
import com.gulfcam.fuelcoupon.user.dto.EmailDto;
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
@Tag(name = "Mouvement en stock")
@RequestMapping("/api/v1.0/stockmovement")
@Slf4j
public class StockMovementRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    IStockMovementService iStockMovementService;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IItemService iItemService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStokMovementRepo iStokMovementRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "Ordre de transfert de cartons inter magasins", tags = "Mouvement en stock", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = StockMovement.class)))),
            @ApiResponse(responseCode = "404", description = "StockMovement not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addStockMovement(@Valid @RequestBody CreateStockMovementDTO createStockMovementDTO) {

        Store store1 = new Store();
        Storehouse storehouse1 = new Storehouse();
        Store store2 = new Store();
        Storehouse storehouse2 = new Storehouse();
        if (createStockMovementDTO.getIdStore1() != null) {
            if(!iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store1 = iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).get();
        }
        if (createStockMovementDTO.getIdStore1() != null) {
            if(!iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store2 = iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).get();
        }
        if (createStockMovementDTO.getIdStoreHouseStockage1() != null) {
            if(!iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage1()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse1 = iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage1()).get();
        }
        if (createStockMovementDTO.getIdStoreHouseStockage2() != null) {
            if(!iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage2()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse2 = iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage2()).get();
        }
        StockMovement stockMovement = new StockMovement();
        stockMovement.setInternalReference(jwtUtils.generateInternalReference());
        stockMovement.setType(createStockMovementDTO.getType());
        stockMovement.setIdStore1(createStockMovementDTO.getIdStore1());
        stockMovement.setIdStore2(createStockMovementDTO.getIdStore2());
        stockMovement.setIdStoreHouse1(createStockMovementDTO.getIdStoreHouseStockage1());
        stockMovement.setIdStoreHouse2(createStockMovementDTO.getIdStoreHouseStockage2());

        iStockMovementService.createStockMovement(stockMovement);

        Item item = new Item();

        TypeVoucher typeVoucher = iTypeVoucherService.getTypeVoucherByAmountEquals(createStockMovementDTO.getTypeVoucher()).get();
        item.setQuantityCarton(-createStockMovementDTO.getQuantityCarton());
        item.setIdTypeVoucher(typeVoucher.getInternalReference());
        item.setQuantityNotebook(0);
        item.setIdStoreHouse(createStockMovementDTO.getIdStoreHouseStockage1());
        item.setInternalReference(jwtUtils.generateInternalReference());
        Status statusItemStockage1 = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        item.setStatus(statusItemStockage1);
        item.setCreatedAt(LocalDateTime.now());

        iItemService.createItem(item);

        item = new Item();
        item.setQuantityCarton(createStockMovementDTO.getQuantityCarton());
        item.setIdTypeVoucher(typeVoucher.getInternalReference());
        item.setQuantityNotebook(0);
        item.setIdStoreHouse(createStockMovementDTO.getIdStoreHouseStockage1());
        item.setInternalReference(jwtUtils.generateInternalReference());
        Status statusItemStockage2 = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        item.setStatus(statusItemStockage2);
        item.setCreatedAt(LocalDateTime.now());

        iItemService.createItem(item);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("quantityCarton", createStockMovementDTO.getQuantityCarton());
        emailProps.put("typevoucher", typeVoucher.getAmount());
        emailProps.put("quantityNotebook", 0);
        emailProps.put("quantityCoupon", 0);
        emailProps.put("storehouseStockage1", storehouse1.getInternalReference()+" - "+storehouse1.getType()+" - "+storehouse1.getName());
        emailProps.put("storehouseStockage2", storehouse2.getInternalReference()+" - "+storehouse2.getType()+" - "+storehouse2.getName());
        emailProps.put("store", store1.getInternalReference()+" - "+store1.getLocalization());

        if(createStockMovementDTO.getIdStoreKeeper() != null){
            Users storeKeeper = iUserService.getByInternalReference(createStockMovementDTO.getIdStoreKeeper());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ORDER_TRANSFER, ApplicationConstant.TEMPLATE_EMAIL_ORDER_TRANSFER));
        }

        return ResponseEntity.ok(stockMovement);
    }


    @Operation(summary = "Modification des informations pour un Mouvement en stock", tags = "Mouvement en stock", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = StockMovement.class)))),
            @ApiResponse(responseCode = "404", description = "StockMovement not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateStockMovement(@Valid @RequestBody CreateStockMovementDTO createStockMovementDTO, @PathVariable Long internalReference) {

        if (!iStockMovementService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.stockMovement_exists", null, LocaleContextHolder.getLocale())));
        }
        StockMovement stockMovement = iStockMovementService.getByInternalReference(internalReference).get();
        Store store1 = new Store();
        Storehouse storehouse1 = new Storehouse();
        Store store2 = new Store();
        Storehouse storehouse2 = new Storehouse();
        if (createStockMovementDTO.getIdStore1() != null) {
            if(!iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store1 = iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).get();
        }
        if (createStockMovementDTO.getIdStore1() != null) {
            if(!iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store2 = iStoreService.getByInternalReference(createStockMovementDTO.getIdStore1()).get();
        }
        if (createStockMovementDTO.getIdStoreHouseStockage1() != null) {
            if(!iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage1()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse1 = iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage1()).get();
        }
        if (createStockMovementDTO.getIdStoreHouseStockage2() != null) {
            if(!iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage2()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse2 = iStorehouseService.getByInternalReference(createStockMovementDTO.getIdStoreHouseStockage2()).get();
        }
        if (createStockMovementDTO.getIdStore1() != null)
            stockMovement.setIdStore1(createStockMovementDTO.getIdStore1());
        if (createStockMovementDTO.getIdStore2() != null)
            stockMovement.setIdStore2(createStockMovementDTO.getIdStore2());
        if (createStockMovementDTO.getIdStoreHouseStockage1() != null)
            stockMovement.setIdStoreHouse1(createStockMovementDTO.getIdStoreHouseStockage1());
        if (createStockMovementDTO.getIdStoreHouseStockage2() != null)
            stockMovement.setIdStoreHouse2(createStockMovementDTO.getIdStoreHouseStockage2());
        stockMovement.setType(createStockMovementDTO.getType());

        iStockMovementService.createStockMovement(stockMovement);

        return ResponseEntity.ok(stockMovement);
    }

    @Operation(summary = "Recupérer Un Mouvement en stock par sa reference interne", tags = "Mouvement en stock", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = StockMovement.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<StockMovement> getStockMovementByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iStockMovementService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer un Mouvement en stock", tags = "Mouvement en stock", responses = {
            @ApiResponse(responseCode = "200", description = "StockMovement deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteStockMovement(@PathVariable Long internalReference) {
        StockMovement stockMovement = iStockMovementService.getByInternalReference(internalReference).get();
        iStockMovementService.deleteStockMovement(stockMovement);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Mouvements en stock", tags = "Mouvement en stock", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "StockMovement not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllStockMovements(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<StockMovement> list = iStockMovementService.getAllStockMovements(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
