package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.store.dto.CreateCartonDTO;
import com.gulfcam.fuelcoupon.store.dto.CreateStockMovementDTO;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.StockMovement;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.repository.IStokMovementRepo;
import com.gulfcam.fuelcoupon.store.service.ICartonService;
import com.gulfcam.fuelcoupon.store.service.IStockMovementService;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

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
    IStatusRepo iStatusRepo;

    @Autowired
    IStokMovementRepo iStokMovementRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un Mouvement en stock", tags = "Mouvement en stock", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = StockMovement.class)))),
            @ApiResponse(responseCode = "404", description = "StockMovement not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addStockMovement(@Valid @RequestBody CreateStockMovementDTO createStockMovementDTO) {

        StockMovement stockMovement = new StockMovement();
        stockMovement.setInternalReference(jwtUtils.generateInternalReference());
        stockMovement.setIdStore1(createStockMovementDTO.getIdStore1());
        stockMovement.setIdStore2(createStockMovementDTO.getIdStore2());
        stockMovement.setIdStoreHouse1(createStockMovementDTO.getIdStoreHouse1());
        stockMovement.setIdStoreHouse2(createStockMovementDTO.getIdStoreHouse2());

        iStockMovementService.createStockMovement(stockMovement);
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

        StockMovement stockMovement = iStockMovementService.getByInternalReference(internalReference).get();
        if (stockMovement.getId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.stockMovement_exists", null, LocaleContextHolder.getLocale())));
        }
        stockMovement.setIdStore1(createStockMovementDTO.getIdStore1());
        stockMovement.setIdStore2(createStockMovementDTO.getIdStore2());
        stockMovement.setIdStoreHouse1(createStockMovementDTO.getIdStoreHouse1());
        stockMovement.setIdStoreHouse2(createStockMovementDTO.getIdStoreHouse2());

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
