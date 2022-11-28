package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.store.dto.CreateRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import com.gulfcam.fuelcoupon.store.repository.IRequestOppositionRepo;
import com.gulfcam.fuelcoupon.store.service.IRequestOppositionService;
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

@RestController
@Tag(name = "Demande d'opposition")
@RequestMapping("/api/v1.0/requestopposition")
@Slf4j
public class RequestOppositionRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    IRequestOppositionService iRequestOppositionService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IRequestOppositionRepo iRequestOppositionRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour une Demande d'opposition", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addRequestOpposition(@Valid @RequestBody CreateRequestOppositionDTO createRequestOppositionDTO) {

        Users managerCoupon = new Users();
        Users serviceClient = new Users();

        if (createRequestOppositionDTO.getIdManagerCoupon() != null) {
            managerCoupon = iUserService.getByInternalReference(createRequestOppositionDTO.getIdManagerCoupon());
            if(managerCoupon.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        if (createRequestOppositionDTO.getIdServiceClient() != null) {
            serviceClient = iUserService.getByInternalReference(createRequestOppositionDTO.getIdServiceClient());
            if(serviceClient.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        RequestOpposition requestOpposition = new RequestOpposition();
        requestOpposition.setInternalReference(jwtUtils.generateInternalReference());
        requestOpposition.setCreatedAt(LocalDateTime.now());
        requestOpposition.setDescription(createRequestOppositionDTO.getDescription());
        requestOpposition.setReason(createRequestOppositionDTO.getReason());
        requestOpposition.setIdManagerCoupon(createRequestOppositionDTO.getIdManagerCoupon());
        requestOpposition.setIdServiceClient(createRequestOppositionDTO.getIdServiceClient());

        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        requestOpposition.setStatus(status);

        iRequestOppositionService.createRequestOpposition(requestOpposition);
        return ResponseEntity.ok(requestOpposition);
    }

    @Operation(summary = "Modification des informations pour une Demande d'opposition", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateRequestOpposition(@Valid @RequestBody CreateRequestOppositionDTO createRequestOppositionDTO, @PathVariable Long internalReference) {

        if (!iRequestOppositionService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.requestopposition_exists", null, LocaleContextHolder.getLocale())));
        }
        RequestOpposition requestOpposition = iRequestOppositionService.getByInternalReference(internalReference).get();

        Users managerCoupon = new Users();
        Users serviceClient = new Users();

        if (createRequestOppositionDTO.getIdManagerCoupon() != null) {
            managerCoupon = iUserService.getByInternalReference(createRequestOppositionDTO.getIdManagerCoupon());
            if(managerCoupon.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        if (createRequestOppositionDTO.getIdServiceClient() != null) {
            serviceClient = iUserService.getByInternalReference(createRequestOppositionDTO.getIdServiceClient());
            if(serviceClient.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        requestOpposition.setUpdateAt(LocalDateTime.now());
        requestOpposition.setDescription(createRequestOppositionDTO.getDescription());
        requestOpposition.setReason(createRequestOppositionDTO.getReason());
        if (createRequestOppositionDTO.getIdManagerCoupon() != null)
            requestOpposition.setIdManagerCoupon(createRequestOppositionDTO.getIdManagerCoupon());
        if (createRequestOppositionDTO.getIdServiceClient() != null)
            requestOpposition.setIdServiceClient(createRequestOppositionDTO.getIdServiceClient());

        iRequestOppositionService.createRequestOpposition(requestOpposition);

        return ResponseEntity.ok(requestOpposition);
    }

    @Operation(summary = "Recupérer la liste des Demandes d'opposition par Service client", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/serviceclient/{idServiceClient:[0-9]+}")
    public ResponseEntity<Page<RequestOpposition>> getRequestOppositionsByIdServiceClient(@PathVariable Long idServiceClient,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idServiceClient") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<RequestOpposition> requestOppositions = iRequestOppositionService.getRequestOppositionsByIdServiceClient(idServiceClient,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(requestOppositions);
    }

    @Operation(summary = "Recupérer la liste des Demandes d'opposition par Gestion de coupon", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/managercoupon/{idManagerCoupon:[0-9]+}")
    public ResponseEntity<Page<RequestOpposition>> getRequestOppositionsByIdManagerCoupon(@PathVariable Long idManagerCoupon,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idManagerCoupon") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<RequestOpposition> requestOppositions = iRequestOppositionService.getRequestOppositionsByIdManagerCoupon(idManagerCoupon,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(requestOppositions);
    }

    @Operation(summary = "Recupérer Un Demande d'opposition par sa reference interne", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<RequestOpposition> getRequestOppositionById(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iRequestOppositionService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer une Demande d'opposition", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "200", description = "RequestOpposition deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteRequestOpposition(@PathVariable Long internalReference) {
        RequestOpposition requestOpposition = iRequestOppositionService.getByInternalReference(internalReference).get();
        iRequestOppositionService.deleteRequestOpposition(requestOpposition);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Demandes d'opposition", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllRequestOppositions(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<RequestOpposition> list = iRequestOppositionService.getAllRequestOppositions(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
