package com.gulfcam.fuelcoupon.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.dto.CreateTypeVoucherDTO;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.repository.ITypeVoucherRepo;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
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
import net.minidev.json.JSONObject;
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
@Tag(name = "Type de bon")
@RequestMapping("/api/v1.0/typevoucher")
@Slf4j
public class TypeVoucherRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;


    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    ITypeVoucherRepo iTypeVoucherRepo;

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
    @Operation(summary = "création des informations pour un Type de bon", tags = "Type de bon", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = TypeVoucher.class)))),
            @ApiResponse(responseCode = "404", description = "typeVoucher not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addTypeVoucher(@Valid @RequestBody CreateTypeVoucherDTO createTypeVoucherDTO) throws JsonProcessingException {

        TypeVoucher typeVoucher = new TypeVoucher();
        typeVoucher.setInternalReference(jwtUtils.generateInternalReference());
        typeVoucher.setCreatedAt(LocalDateTime.now());
        typeVoucher.setDesignation(aes.decrypt(key, createTypeVoucherDTO.getDesignation()));
        typeVoucher.setAmount(Float.parseFloat(aes.decrypt(key, createTypeVoucherDTO.getAmount())));
        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        typeVoucher.setStatus(status);

        iTypeVoucherService.createTypeVoucher(typeVoucher);

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(typeVoucher);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Modification des informations pour un Type de bon", tags = "Type de bon", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = TypeVoucher.class)))),
            @ApiResponse(responseCode = "404", description = "TypeVoucher not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateTypeVoucher(@Valid @RequestBody CreateTypeVoucherDTO createTypeVoucherDTO, @PathVariable String internalReference) throws JsonProcessingException {

        if (!iTypeVoucherService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.typeVoucher_exists", null, LocaleContextHolder.getLocale())));
        }
        TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        typeVoucher.setUpdateAt(LocalDateTime.now());
        typeVoucher.setDesignation(aes.decrypt(key, createTypeVoucherDTO.getDesignation()));
        typeVoucher.setAmount(Float.parseFloat(aes.decrypt(key, createTypeVoucherDTO.getAmount())));

        iTypeVoucherService.createTypeVoucher(typeVoucher);

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(typeVoucher);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Un Type de bon par sa reference interne", tags = "Type de bon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = TypeVoucher.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getTypeVoucherById(@PathVariable String internalReference) throws JsonProcessingException {
        TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(typeVoucher);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Supprimer un Type de bon", tags = "Type de bon", responses = {
            @ApiResponse(responseCode = "200", description = "TypeVoucher deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteTypeVoucher(@PathVariable Long internalReference) {
        TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(internalReference).get();
        iTypeVoucherService.deleteTypeVoucher(typeVoucher);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Type de bon", tags = "Type de bon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "TypeVoucher not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getTypeVouchers(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<TypeVoucher> list = iTypeVoucherService.getAllTypeVouchers(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }
    }
