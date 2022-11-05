package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.store.dto.CreateCartonDTO;
import com.gulfcam.fuelcoupon.store.dto.CreateCouponDTO;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.repository.ICouponRepo;
import com.gulfcam.fuelcoupon.store.service.ICartonService;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
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
@Tag(name = "Coupon")
@RequestMapping("/api/v1.0/coupon")
@Slf4j
public class CouponRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;


    @Autowired
    ICouponService iCouponService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    ICouponRepo iCouponRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un Coupon", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addCoupon(@Valid @RequestBody CreateCouponDTO createCouponDTO) {

        if (iCouponService.existsCouponBySerialNumber(createCouponDTO.getSerialNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.serial_exists", null, LocaleContextHolder.getLocale())));
        }

        Coupon coupon = new Coupon();
        coupon.setInternalReference(jwtUtils.generateInternalReference());
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setSerialNumber(createCouponDTO.getSerialNumber());
        coupon.setIdClient(createCouponDTO.getIdClient());
        coupon.setIdNotebook(createCouponDTO.getIdNotebook());
        coupon.setIdStation(createCouponDTO.getIdStation());
        coupon.setIdTicket(createCouponDTO.getIdTicket());

        iCouponService.createCoupon(coupon);
        return ResponseEntity.ok(coupon);
    }

    @Operation(summary = "Modification des informations pour un Coupon", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateCoupon(@Valid @RequestBody CreateCouponDTO createCouponDTO, @PathVariable Long internalReference) {

        Coupon coupon = iCouponService.getByInternalReference(internalReference).get();
        if (coupon.getId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
        }
        coupon.setUpdateAt(LocalDateTime.now());
        coupon.setSerialNumber(createCouponDTO.getSerialNumber());
        coupon.setIdClient(createCouponDTO.getIdClient());
        coupon.setIdNotebook(createCouponDTO.getIdNotebook());
        coupon.setIdStation(createCouponDTO.getIdStation());
        coupon.setIdTicket(createCouponDTO.getIdTicket());

        iCouponService.createCoupon(coupon);

        return ResponseEntity.ok(coupon);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Client", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/client/{idClient:[0-9]+}")
    public ResponseEntity<Page<Coupon>> getCouponsByIdClient(@PathVariable Long idClient,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                             @RequestParam(required = false, defaultValue = "idClient") String sort,
                                                             @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Coupon> cartons = iCouponService.getCouponsByIdClient(idClient,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(cartons);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Station", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/station/{idStation:[0-9]+}")
    public ResponseEntity<Page<Coupon>> getCouponsByIdStation(@PathVariable Long idStation,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                             @RequestParam(required = false, defaultValue = "idStation") String sort,
                                                             @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Coupon> cartons = iCouponService.getCouponsByIdStation(idStation,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(cartons);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Carnet", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/notebook/{idNotebook:[0-9]+}")
    public ResponseEntity<Page<Coupon>> getCouponsByIdNotebook(@PathVariable Long idNotebook,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                             @RequestParam(required = false, defaultValue = "idNotebook") String sort,
                                                             @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Coupon> cartons = iCouponService.getCouponsByIdNotebook(idNotebook,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(cartons);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Ticket", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/ticket/{idTicket:[0-9]+}")
    public ResponseEntity<Page<Coupon>> getCouponsByIdTicket(@PathVariable Long idTicket,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                             @RequestParam(required = false, defaultValue = "idTicket") String sort,
                                                             @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Coupon> cartons = iCouponService.getCouponsByIdTicket(idTicket,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(cartons);
    }

    @Operation(summary = "Recupérer Un Coupon par sa reference interne", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Coupon> getCouponByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iCouponService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Recupérer Un Coupon par son Numéro de série", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<Coupon> getCouponByPinCode(@PathVariable String serialNumber) {
        return ResponseEntity.ok(iCouponService.getCouponBySerialNumber(serialNumber).get());
    }

    @Operation(summary = "Supprimer un Coupon", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", description = "Coupon deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteCoupon(@PathVariable Long internalReference) {
        Coupon carton = iCouponService.getByInternalReference(internalReference).get();
        iCouponService.deleteCoupon(carton);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Coupons", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllCartons(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Coupon> list = iCouponService.getAllCoupons(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
