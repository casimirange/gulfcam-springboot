package com.gulfcam.fuelcoupon.order.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.dto.CreateItemDTO;
import com.gulfcam.fuelcoupon.order.dto.CreateUnitDTO;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.entity.Unit;
import com.gulfcam.fuelcoupon.order.repository.IItemRepo;
import com.gulfcam.fuelcoupon.order.repository.IUnitRepo;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.order.service.IUnitService;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
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

@RestController
@Tag(name = "Unité")
@RequestMapping("/api/v1.0/unit")
@Slf4j
public class UnitRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    IUnitService iUnitService;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IUnitRepo iUnitRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour une Unité", tags = "Unité", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Unit.class)))),
            @ApiResponse(responseCode = "404", description = "Unit not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addUnit(@Valid @RequestBody CreateUnitDTO createUnitDTO) {

        Store store= new Store();
        TypeVoucher typeVoucher = new TypeVoucher();
        if (createUnitDTO.getIdTypeVoucher()  != null) {
            if(!iTypeVoucherService.getByInternalReference(createUnitDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typevoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createUnitDTO.getIdTypeVoucher()).get();

        }

        if (createUnitDTO.getIdStore()  != null) {
            if(!iStoreService.getByInternalReference(createUnitDTO.getIdStore()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store = iStoreService.getByInternalReference(createUnitDTO.getIdStore()).get();

        }
        Unit unit = new Unit();
        unit.setInternalReference(jwtUtils.generateInternalReference());
        unit.setCreatedAt(LocalDateTime.now());
        unit.setIdTypeVoucher(createUnitDTO.getIdTypeVoucher() );
        unit.setIdStore(createUnitDTO.getIdStore());
        unit.setQuantityNotebook(createUnitDTO.getQuantityNotebook());

        Status status = iStatusRepo.findByName(EStatus.STORE_ENABLE).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.STORE_ENABLE +  "  not found"));
        unit.setStatus(status);

        iUnitService.createUnit(unit);
        return ResponseEntity.ok(unit);
    }

    @Operation(summary = "Modification des informations pour une Unité", tags = "Unité", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Unit.class)))),
            @ApiResponse(responseCode = "404", description = "unit not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateUnit(@Valid @RequestBody CreateUnitDTO createUnitDTO, @PathVariable Long internalReference) {

        if (!iUnitService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.unit_exists", null, LocaleContextHolder.getLocale())));
        }
        Unit unit = iUnitService.getByInternalReference(internalReference).get();
        Store store= new Store();
        TypeVoucher typeVoucher = new TypeVoucher();
        if (createUnitDTO.getIdTypeVoucher()  != null) {
            if(!iTypeVoucherService.getByInternalReference(createUnitDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typevoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createUnitDTO.getIdTypeVoucher()).get();

        }

        if (createUnitDTO.getIdStore()  != null) {
            if(!iStoreService.getByInternalReference(createUnitDTO.getIdStore()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store = iStoreService.getByInternalReference(createUnitDTO.getIdStore()).get();

        }
        unit.setUpdateAt(LocalDateTime.now());
        if (createUnitDTO.getIdTypeVoucher()  != null)
            unit.setIdTypeVoucher(createUnitDTO.getIdTypeVoucher());
        if (createUnitDTO.getIdStore()  != null)
            unit.setIdStore(createUnitDTO.getIdStore());
        unit.setQuantityNotebook(createUnitDTO.getQuantityNotebook());

        iUnitService.createUnit(unit);

        return ResponseEntity.ok(unit);
    }

    @Operation(summary = "Recupérer la liste des Unités par type de bon", tags = "Unité", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Unit.class)))),
            @ApiResponse(responseCode = "404", description = "unit not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/typevoucher/{idTypeVoucher:[0-9]+}")
    public ResponseEntity<Page<Unit>> getUnitsByIdTypeVoucher(@PathVariable Long idTypeVoucher,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idTypeVoucher") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Unit> units = iUnitService.getUnitsByIdTypeVoucher(idTypeVoucher,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Recupérer la liste des Unités par Magasin", tags = "Unité", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Unit.class)))),
            @ApiResponse(responseCode = "404", description = "unit not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/store/{idStore:[0-9]+}")
    public ResponseEntity<Page<Unit>> getItemsByIdStoreHouse(@PathVariable Long idStore,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idTypeVoucher") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Unit> units = iUnitService.getUnitsByIdStore(idStore,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Recupérer Une Unité par sa reference interne", tags = "Unité", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Unit.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iUnitService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer une Unité", tags = "Unité", responses = {
            @ApiResponse(responseCode = "200", description = "Unit deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteUnit(@PathVariable Long internalReference) {
        Unit unit = iUnitService.getByInternalReference(internalReference).get();
        iUnitService.deleteUnit(unit);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Unités", tags = "Unité", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Unit not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getUnits(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Unit> list = iUnitService.getAllUnits(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
