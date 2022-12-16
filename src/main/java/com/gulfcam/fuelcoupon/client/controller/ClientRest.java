package com.gulfcam.fuelcoupon.client.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.dto.CreateClientDTO;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.entity.ETypeClient;
import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import com.gulfcam.fuelcoupon.client.repository.IClientRepo;
import com.gulfcam.fuelcoupon.client.repository.ITypeClientRepo;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.user.dto.EmailDto;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
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
import java.util.*;

@RestController
@Tag(name = "Client")
@RequestMapping("/api/v1.0/client")
@Slf4j
public class ClientRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;


    @Autowired
    IClientService iClientService;

    @Autowired
    IClientRepo iClientRepo;

    @Autowired
    ITypeClientRepo iTypeClientRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un client", tags = "Client", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addClient(@Valid @RequestBody CreateClientDTO createClientDTO) {

        if (iClientService.existsByEmail(createClientDTO.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
        }
        if (iClientService.existsByGulfCamAccountNumber(createClientDTO.getGulfcamAccountNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.num_gulfcam_exists", null, LocaleContextHolder.getLocale())));
        }
        Client client = new Client();
        client.setInternalReference(jwtUtils.generateInternalReference());
        client.setCreatedAt(LocalDateTime.now());
        client.setAddress(createClientDTO.getAddress());
        client.setCompanyName(createClientDTO.getCompanyName());
        client.setCompleteName(createClientDTO.getCompleteName());
        client.setPhone(createClientDTO.getPhone());
        client.setRCCM(createClientDTO.getRCCM());
        client.setGulfcamAccountNumber(createClientDTO.getGulfcamAccountNumber());
        client.setEmail(createClientDTO.getEmail());
        TypeClient typeAccount = iTypeClientRepo.findByName(ETypeClient.valueOf(createClientDTO.getTypeClient().toUpperCase())).orElseThrow(()-> new ResourceNotFoundException("Type de Client:  "  +  createClientDTO.getTypeClient() +  "  not found"));
        client.setTypeClient(typeAccount);

        iClientService.createClient(client);
        if(createClientDTO.getEmail() != null ){
            Map<String, Object> emailProps = new HashMap<>();
            emailProps.put("firstname", createClientDTO.getCompleteName());

            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, createClientDTO.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_USER, ApplicationConstant.TEMPLATE_EMAIL_NEW_USER));
            log.info("Email  send successfull for user: " + createClientDTO.getEmail());
        }
        return ResponseEntity.ok(client);
    }

    @Operation(summary = "modification des informations pour un client", tags = "Client", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateClient(@Valid @RequestBody CreateClientDTO createClientDTO, @PathVariable Long internalReference) {

        if (!iClientService.getClientByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
        }
        Client client = iClientService.getClientByInternalReference(internalReference).get();
        client.setUpdateAt(LocalDateTime.now());
        client.setAddress(createClientDTO.getAddress());
        client.setCompanyName(createClientDTO.getCompanyName());
        client.setCompleteName(createClientDTO.getCompleteName());
        client.setPhone(createClientDTO.getPhone());
        client.setRCCM(createClientDTO.getRCCM());
        client.setGulfcamAccountNumber(createClientDTO.getGulfcamAccountNumber());
        client.setEmail(createClientDTO.getEmail());
        TypeClient typeAccount = iTypeClientRepo.findByName(ETypeClient.valueOf(createClientDTO.getTypeClient().toUpperCase())).orElseThrow(()-> new ResourceNotFoundException("Type de Client:  "  +  createClientDTO.getTypeClient() +  "  not found"));
        client.setTypeClient(typeAccount);

        iClientService.createClient(client);

        return ResponseEntity.ok(client);
    }

    @Operation(summary = "Recupérer Un Client par son email", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        return ResponseEntity.ok(iClientService.getClientByEmail(email).get());
    }

    @Operation(summary = "Recupérer Un Client par son Identifiant Gulfcam", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/gulfcamaccountnumber/{gulfcamaccountnumber}")
    public ResponseEntity<Client> getClientByGulfCamAccountNumber(@PathVariable String gulfcamaccountnumber) {
        return ResponseEntity.ok(iClientService.getClientByGulfCamAccountNumber(gulfcamaccountnumber).get());
    }

    @Operation(summary = "Recupérer Un Client par sa reférence interne", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Client> getClientById(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iClientService.getClientByInternalReference(internalReference).get());
    }

    @Operation(summary = "Recupérer la liste des client comme son nom complete", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/like/{completeName}")
    public ResponseEntity<?> getClientsByCompanyNameLike(@PathVariable String completeName) {
        List<Client> clients = iClientService.getClientsByCompanyNameLike(completeName);
        return ResponseEntity.ok(clients);
    }

    @Operation(summary = "Supprimer un client", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", description = "Client deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('USER') or hasRole('USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteClient(@PathVariable Long internalReference) {
        Client offerJob = iClientService.getClientByInternalReference(internalReference).get();
        iClientService.deleteClient(offerJob);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Clients", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getClients(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Client> list = iClientService.getAllClients(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
