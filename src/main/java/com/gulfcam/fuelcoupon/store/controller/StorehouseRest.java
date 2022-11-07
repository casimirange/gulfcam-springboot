package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.store.dto.CreateStorehouseDTO;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.repository.IStorehouseRepo;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@Tag(name = "Entrepôt")
@RequestMapping("/api/v1.0/storehouse")
@Slf4j
public class StorehouseRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IStorehouseRepo iStorehouseRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un Entrepôt", tags = "Entrepôt", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Storehouse.class)))),
            @ApiResponse(responseCode = "404", description = "Storehouse not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addStorehouse(@Valid @RequestBody CreateStorehouseDTO createStorehouseDTO) {

        Store store = new Store();
        if (createStorehouseDTO.getIdStore() != null) {
            if(!iStoreService.getByInternalReference(createStorehouseDTO.getIdStore()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store = iStoreService.getByInternalReference(createStorehouseDTO.getIdStore()).get();
        }

        Storehouse storehouse = new Storehouse();
        storehouse.setInternalReference(jwtUtils.generateInternalReference());
        storehouse.setCreateAt(LocalDate.now());
        storehouse.setIdStore(createStorehouseDTO.getIdStore());
        storehouse.setType(createStorehouseDTO.getType());

        Status status = iStatusRepo.findByName(EStatus.STORE_ENABLE).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.STORE_ENABLE +  "  not found"));
        storehouse.setStatus(status);

        iStorehouseService.createStorehouse(storehouse);
        return ResponseEntity.ok(storehouse);
    }

    @Operation(summary = "Modification des informations pour un Entrepôt", tags = "Entrepôt", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Storehouse.class)))),
            @ApiResponse(responseCode = "404", description = "Storehouse not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateStorehouse(@Valid @RequestBody CreateStorehouseDTO createStorehouseDTO, @PathVariable Long internalReference) {

        if (!iStorehouseService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
        }
        Storehouse storehouse = iStorehouseService.getByInternalReference(internalReference).get();
        Store store = new Store();
        if (createStorehouseDTO.getIdStore() != null) {
            if(!iStoreService.getByInternalReference(createStorehouseDTO.getIdStore()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store = iStoreService.getByInternalReference(createStorehouseDTO.getIdStore()).get();
        }
        storehouse.setUpdateAt(LocalDate.now());
        if (createStorehouseDTO.getIdStore() != null)
            storehouse.setIdStore(createStorehouseDTO.getIdStore());
        storehouse.setType(createStorehouseDTO.getType());

        iStorehouseService.createStorehouse(storehouse);

        return ResponseEntity.ok(storehouse);
    }

    @Operation(summary = "Recupérer la liste des Entrepôts par magasin", tags = "Entrepôt", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Storehouse.class)))),
            @ApiResponse(responseCode = "404", description = "Storehouse not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/store/{idStore:[0-9]+}")
    public ResponseEntity<Page<Storehouse>> getStorehousesByIdStore(@PathVariable Long idStore,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idStore") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Storehouse> storehouses = iStorehouseService.getStorehousesByIdStore(idStore,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(storehouses);
    }

    @Operation(summary = "Recupérer Un Entrepôt par sa reference interne", tags = "Entrepôt", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Storehouse.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Storehouse> getStorehouseByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iStorehouseService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer un Entrepôt", tags = "Entrepôt", responses = {
            @ApiResponse(responseCode = "200", description = "Storehouse deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteStorehouse(@PathVariable Long internalReference) {
        Storehouse storehouse = iStorehouseService.getByInternalReference(internalReference).get();
        iStorehouseService.deleteStorehouse(storehouse);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Entrepôts", tags = "Entrepôt", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Storehouse not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllStorehouses(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Storehouse> list = iStorehouseService.getAllStorehouses(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
