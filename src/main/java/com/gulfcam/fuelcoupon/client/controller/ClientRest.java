package com.gulfcam.fuelcoupon.client.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.dto.CreateClientDTO;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.entity.ETypeClient;
import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import com.gulfcam.fuelcoupon.client.repository.IClientRepo;
import com.gulfcam.fuelcoupon.client.repository.ITypeClientRepo;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
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
import net.minidev.json.JSONObject;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
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

    JsonMapper jsonMapper = new JsonMapper();
    AESUtil aes = new AESUtil();
    @Value("${app.key}")
    String key;
    @Operation(summary = "création des informations pour un client", tags = "Client", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addClient(@Valid @RequestBody CreateClientDTO createClientDTO) throws JsonProcessingException {

        if (!createClientDTO.getEmail().isEmpty() && iClientService.existsByEmail(aes.decrypt(key, createClientDTO.getEmail()))) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
        }
//        if (!createClientDTO.getNiu().isEmpty() && iClientService.existsByNiu(aes.decrypt(key, createClientDTO.getNiu()))) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.niu_exists", null, LocaleContextHolder.getLocale())));
//        }
        if (iClientService.existsByGulfCamAccountNumber(aes.decrypt(key, createClientDTO.getGulfcamAccountNumber()))) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.num_gulfcam_exists", null, LocaleContextHolder.getLocale())));
        }
        Client client = new Client();
        client.setInternalReference(jwtUtils.generateInternalReference());
        client.setCreatedAt(LocalDateTime.now());
        client.setAddress(aes.decrypt(key, createClientDTO.getAddress()));
        client.setCompanyName(createClientDTO.getCompanyName().isEmpty() ? "" : aes.decrypt(key, createClientDTO.getCompanyName()));
        client.setCompleteName(aes.decrypt(key, createClientDTO.getCompleteName()));
        client.setPhone(aes.decrypt(key, createClientDTO.getPhone()));
        client.setRCCM(createClientDTO.getNiu().isEmpty() ? "" : aes.decrypt(key, createClientDTO.getRCCM()));
        client.setNiu(createClientDTO.getRCCM().isEmpty() ? null : aes.decrypt(key, createClientDTO.getNiu()));
        client.setGulfcamAccountNumber(aes.decrypt(key, createClientDTO.getGulfcamAccountNumber()));
        client.setEmail(createClientDTO.getEmail().isEmpty() ? "" : aes.decrypt(key, createClientDTO.getEmail()));
        TypeClient typeAccount = iTypeClientRepo.findByName(ETypeClient.valueOf(aes.decrypt(key, createClientDTO.getTypeClient()).toUpperCase())).orElseThrow(()-> new ResourceNotFoundException("Type de Client:  "  +  aes.decrypt(key, createClientDTO.getTypeClient()) +  "  not found"));
        client.setTypeClient(typeAccount);

        iClientService.createClient(client);
        if(createClientDTO.getEmail() != null ){
            Map<String, Object> emailProps = new HashMap<>();
            emailProps.put("firstname", aes.decrypt(key, createClientDTO.getCompleteName()));

            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, aes.decrypt(key, createClientDTO.getEmail()), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_USER, ApplicationConstant.TEMPLATE_EMAIL_NEW_USER));
            log.info("Email  send successfull for user: " + aes.decrypt(key, createClientDTO.getEmail()));
        }
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(client);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "modification des informations pour un client", tags = "Client", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateClient(@Valid @RequestBody CreateClientDTO createClientDTO, @PathVariable String internalReference) throws JsonProcessingException {
        log.info("client "+internalReference);
        log.info("décrypté "+aes.decrypt(key, internalReference));
        Client client = iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        log.info("client "+client.getInternalReference());
        if (!iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
        }
//        if (!createClientDTO.getNiu().isEmpty() && (!client.getNiu().equals(aes.decrypt(key, createClientDTO.getNiu())) && iClientService.existsByNiu(aes.decrypt(key, createClientDTO.getNiu())))) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.niu_exists", null, LocaleContextHolder.getLocale())));
//        }
//        if (!createClientDTO.getEmail().isEmpty() && !client.getEmail().equals(aes.decrypt(key, createClientDTO.getEmail())) && iClientService.existsByEmail(aes.decrypt(key, createClientDTO.getEmail()))) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
//        }

        client.setUpdateAt(LocalDateTime.now());
        client.setAddress(aes.decrypt(key, createClientDTO.getAddress()));
        client.setCompanyName(createClientDTO.getCompanyName().isEmpty() ? "" : aes.decrypt(key, createClientDTO.getCompanyName()));
        client.setCompleteName(aes.decrypt(key, createClientDTO.getCompleteName()));
        client.setPhone(aes.decrypt(key, createClientDTO.getPhone()));
        client.setRCCM(createClientDTO.getRCCM().isEmpty() ? "" : aes.decrypt(key, createClientDTO.getRCCM()));
        client.setNiu(createClientDTO.getNiu().isEmpty() ? "" : aes.decrypt(key, createClientDTO.getNiu()));
        client.setGulfcamAccountNumber(aes.decrypt(key, createClientDTO.getGulfcamAccountNumber()));
        client.setEmail(createClientDTO.getEmail().isEmpty() ? "" : aes.decrypt(key, createClientDTO.getEmail()));
        TypeClient typeAccount = iTypeClientRepo.findByName(ETypeClient.valueOf(aes.decrypt(key, createClientDTO.getTypeClient()).toUpperCase())).orElseThrow(()-> new ResourceNotFoundException("Type de Client:  "  +  aes.decrypt(key, createClientDTO.getTypeClient()) +  "  not found"));
        client.setTypeClient(typeAccount);

        iClientService.createClient(client);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(client);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Un Client par son email", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getClientByEmail(@PathVariable String email) throws JsonProcessingException {
        Client client = iClientService.getClientByEmail(aes.decrypt(key, email)).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(client);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Un Client par son Identifiant Gulfcam", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/gulfcamaccountnumber/{gulfcamaccountnumber}")
    public ResponseEntity<?> getClientByGulfCamAccountNumber(@PathVariable String gulfcamaccountnumber) throws JsonProcessingException {
        Client client = iClientService.getClientByGulfCamAccountNumber(aes.decrypt(key, gulfcamaccountnumber)).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(client);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Un Client par sa reférence interne", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getClientById(@PathVariable String internalReference) throws JsonProcessingException {
        Client client = iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(client);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer la liste des client comme son nom complete", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/like/{completeName}")
    public ResponseEntity<?> getClientsByCompleteNameContains(@PathVariable String completeName) throws JsonProcessingException {
        List<Client> clients = iClientService.getClientsByCompleteNameContains(completeName);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(clients);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
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
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<Client> list = iClientService.getAllClients(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Filtrer les Clients", tags = "Client", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filterClients(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                        @RequestParam(required = false, value = "company") String company,
                                        @RequestParam(required = false, value = "name") String clientName,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false, value = "date") LocalDate date,
                                        @RequestParam(required = false, value = "type") String type,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<Client> list = iClientService.filtres(clientName, company, type, date, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }
    }
