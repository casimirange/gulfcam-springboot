package com.gulfcam.fuelcoupon.store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.dto.CreateRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponMailDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import com.gulfcam.fuelcoupon.store.entity.Ticket;
import com.gulfcam.fuelcoupon.store.repository.IRequestOppositionRepo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ITicketService iTicketService;

    @Autowired
    IClientService iClientService;

    @Autowired
    ICouponService iCouponService;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

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
    JsonMapper jsonMapper = new JsonMapper();
    AESUtil aes = new AESUtil();
    @Value("${app.key}")
    String key;
    @Operation(summary = "création des informations pour une Demande d'opposition", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addRequestOpposition(@Valid @RequestBody CreateRequestOppositionDTO createRequestOppositionDTO) throws JsonProcessingException {

        Users salesManager = new Users();
        Users commercialAttache = new Users();
        ResponseCouponMailDTO responseCouponMailDTO;
        Coupon coupon;
        Ticket ticket;
        Client client = new Client();

        if (createRequestOppositionDTO.getIdClient()  != null) {
            if(!iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdClient()))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
            client = iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdClient()))).get();

        }

        if (createRequestOppositionDTO.getIdSalesManager() != null) {
            salesManager = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdSalesManager())));
            if(salesManager.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        if (createRequestOppositionDTO.getIdCommercialAttache() != null) {
            commercialAttache = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdCommercialAttache())));
            if(commercialAttache.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        RequestOpposition requestOpposition = new RequestOpposition();
        requestOpposition.setInternalReference(jwtUtils.generateInternalReference());
        requestOpposition.setCreatedAt(LocalDateTime.now());
        requestOpposition.setDescription(aes.decrypt(key, createRequestOppositionDTO.getDescription()));
        requestOpposition.setReason(aes.decrypt(key, createRequestOppositionDTO.getReason()));
//        requestOpposition.setIdSalesManager(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdSalesManager())));
        requestOpposition.setIdCommercialAttache(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdCommercialAttache())));
        requestOpposition.setIdClient(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdClient())));
        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        Status statusCouponActived = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.ACTIVATED +  "  not found"));
        requestOpposition.setStatus(status);

        iRequestOppositionService.createRequestOpposition(requestOpposition);
        List<ResponseCouponMailDTO> couponList = new ArrayList<>();
        for (int i = 0; i< createRequestOppositionDTO.getSerialCoupons().size(); i++){
            coupon = iCouponService.getCouponBySerialNumber(aes.decrypt(key, createRequestOppositionDTO.getSerialCoupons().get(i))).get();
            ticket = new Ticket();
            if(client.getInternalReference().equals(coupon.getIdClient()) && coupon.getStatus().equals(statusCouponActived)){
                responseCouponMailDTO= new ResponseCouponMailDTO();
                responseCouponMailDTO.setIdTypeVoucher(iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
                responseCouponMailDTO.setInternalReference(coupon.getInternalReference());
                responseCouponMailDTO.setStatus(coupon.getStatus());
                responseCouponMailDTO.setSerialNumber(coupon.getSerialNumber());
                Status statusCoupon= iStatusRepo.findByName(EStatus.SUSPENDED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.SUSPENDED +  "  not found"));
                coupon.setStatus(statusCoupon);
                coupon.setIdRequestOpposition(requestOpposition.getInternalReference());
                coupon.setUpdateAt(LocalDateTime.now());
                iCouponService.createCoupon(coupon);


                ticket.setIdRequestOpposition(requestOpposition.getInternalReference());
                ticket.setIdCoupon(coupon.getInternalReference());
                ticket.setStatus(status);
                ticket.setInternalReference(jwtUtils.generateInternalReference());
                ticket.setCreatedAt(LocalDateTime.now());
                iTicketService.createTicket(ticket);
                couponList.add(responseCouponMailDTO);
            }
        }

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("client", client.getInternalReference()+" - "+client.getCompleteName()+" - "+client.getEmail()+" - "+client.getTypeClient()+" - "+client.getPhone());
        emailProps.put("description", requestOpposition.getDescription());
        emailProps.put("raison", requestOpposition.getReason());
        emailProps.put("couponList", couponList);
        if(createRequestOppositionDTO.getIdSalesManager() != null){
            Users salesManagerUser = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdSalesManager())));
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, salesManagerUser.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_DEMANDE_OPPOSITION+" #"+requestOpposition.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_DEMANDE_OPPOSITION));
        }
        if(createRequestOppositionDTO.getIdCommercialAttache() != null){
            Users storeKeeper = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdCommercialAttache())));
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_DEMANDE_OPPOSITION+" #"+requestOpposition.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_DEMANDE_OPPOSITION));
        }

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(requestOpposition);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }


    @Operation(summary = "validation d'une Demande d'opposition", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/{internalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> validRequestOpposition(@PathVariable String internalReference, @RequestParam(name = "manager") String idSalesManager) {

        if (!iRequestOppositionService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.requestopposition_exists", null, LocaleContextHolder.getLocale())));
        }
        RequestOpposition requestOpposition = iRequestOppositionService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();

        Client client = iClientService.getClientByInternalReference(requestOpposition.getIdClient()).get();
        Coupon coupon = new Coupon();
        Ticket ticket;
        ResponseCouponMailDTO responseCouponMailDTO;
        requestOpposition.setIdSalesManager(Long.parseLong(aes.decrypt(key, idSalesManager)));

        requestOpposition.setUpdateAt(LocalDateTime.now());
        Status status = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.ACTIVATED +  "  not found"));
        requestOpposition.setStatus(status);

        iRequestOppositionService.createRequestOpposition(requestOpposition);

        List<Coupon> couponList = iCouponService.getCouponsByIdRequestOpposition(requestOpposition.getInternalReference());
        List<ResponseCouponMailDTO> couponResponseMailList = new ArrayList<>();
        for (Coupon item: couponList){
            coupon = item;
            Status statusCoupon= iStatusRepo.findByName(EStatus.CANCELED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CANCELED +  "  not found"));
            coupon.setStatus(statusCoupon);
            coupon.setUpdateAt(LocalDateTime.now());
            iCouponService.createCoupon(coupon);
            responseCouponMailDTO= new ResponseCouponMailDTO();
            responseCouponMailDTO.setIdTypeVoucher(iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponMailDTO.setInternalReference(coupon.getInternalReference());
            responseCouponMailDTO.setStatus(coupon.getStatus());
            responseCouponMailDTO.setSerialNumber(coupon.getSerialNumber());
            couponResponseMailList.add(responseCouponMailDTO);
        }

        List<Ticket> ticketList = iTicketService.getTicketsByIdRequestOpposition(requestOpposition.getInternalReference());
        for (Ticket item: ticketList){
            ticket = item;
            Status statusTicket= iStatusRepo.findByName(EStatus.CANCELED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CANCELED +  "  not found"));
            ticket.setStatus(statusTicket);
            ticket.setUpdateAt(LocalDateTime.now());
            iTicketService.createTicket(ticket);
        }

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("client", client.getInternalReference()+" - "+client.getCompleteName()+" - "+client.getEmail()+" - "+client.getTypeClient()+" - "+client.getPhone());
        emailProps.put("description", requestOpposition.getDescription());
        emailProps.put("raison", requestOpposition.getReason());
        emailProps.put("couponList", couponResponseMailList);
        if(requestOpposition.getIdSalesManager() != null){
            Users storeKeeper = iUserService.getByInternalReference(requestOpposition.getIdSalesManager());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_DEMANDE_OPPOSITION+" #"+requestOpposition+" - VALIDÉE", ApplicationConstant.TEMPLATE_EMAIL_DEMANDE_OPPOSITION));
        }
        if(requestOpposition.getIdCommercialAttache() != null){
            Users storeKeeper = iUserService.getByInternalReference(requestOpposition.getIdCommercialAttache());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_DEMANDE_OPPOSITION+" #"+requestOpposition+" - VALIDÉE", ApplicationConstant.TEMPLATE_EMAIL_DEMANDE_OPPOSITION));
        }
        if(requestOpposition.getIdClient() != null){
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_DEMANDE_OPPOSITION+" #"+requestOpposition+" - VALIDÉE", ApplicationConstant.TEMPLATE_EMAIL_DEMANDE_OPPOSITION));
        }

        return ResponseEntity.ok(requestOpposition);
    }

    @Operation(summary = "Modification des informations pour une Demande d'opposition", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateRequestOpposition(@Valid @RequestBody CreateRequestOppositionDTO createRequestOppositionDTO, @PathVariable String internalReference) throws JsonProcessingException {

        if (!iRequestOppositionService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.requestopposition_exists", null, LocaleContextHolder.getLocale())));
        }
        RequestOpposition requestOpposition = iRequestOppositionService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();

        Users managerCoupon = new Users();
        Users serviceClient = new Users();
        Client client = new Client();

        if (createRequestOppositionDTO.getIdClient()  != null) {
            if(!iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdClient()))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
            client = iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdClient()))).get();

        }
        if (createRequestOppositionDTO.getIdSalesManager() != null) {
            managerCoupon = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdSalesManager())));
            if(managerCoupon.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        if (createRequestOppositionDTO.getIdCommercialAttache() != null) {
            serviceClient = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdCommercialAttache())));
            if(serviceClient.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        requestOpposition.setUpdateAt(LocalDateTime.now());
        requestOpposition.setDescription(aes.decrypt(key, createRequestOppositionDTO.getDescription()));
        requestOpposition.setReason(aes.decrypt(key, createRequestOppositionDTO.getReason()));
        if (createRequestOppositionDTO.getIdSalesManager() != null)
            requestOpposition.setIdSalesManager(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdSalesManager())));
        if (createRequestOppositionDTO.getIdCommercialAttache() != null)
            requestOpposition.setIdCommercialAttache(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdCommercialAttache())));
        if (createRequestOppositionDTO.getIdClient() != null)
            requestOpposition.setIdClient(Long.parseLong(aes.decrypt(key, createRequestOppositionDTO.getIdClient())));

        iRequestOppositionService.createRequestOpposition(requestOpposition);

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(requestOpposition);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
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

    @Operation(summary = "Filtrer la liste des Demandes d'opposition ", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filtre")
    public ResponseEntity<Page<ResponseRequestOppositionDTO>> filterRequestOppositions(
                                                              @RequestParam(required = false, value = "client", defaultValue = "null") String clientName,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)            @RequestParam(required = false, value = "date", defaultValue = "") LocalDate date,
                                                              @RequestParam(required = false, value = "status", defaultValue = "null") String status,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idServiceClient") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<ResponseRequestOppositionDTO> requestOppositions = iRequestOppositionService.filterRequestOppositions(clientName, date, status,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(requestOppositions);
    }

    @Operation(summary = "Filtrer la liste des Demandes d'opposition ", tags = "Demande d'opposition", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filtres(
                                                              @RequestParam(required = false, value = "client") String clientName,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)            @RequestParam(required = false, value = "date") LocalDate date,
                                                              @RequestParam(required = false, value = "status") String status,
                                                              @RequestParam(required = false, value = "commercialAttach") String comA,
                                                              @RequestParam(required = false, value = "salemanager") String saleManager,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "id") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {

        Page<ResponseRequestOppositionDTO> list = iRequestOppositionService.filtres(clientName, date, status, comA, saleManager,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
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
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getRequestOppositionById(@PathVariable String internalReference) throws JsonProcessingException {

        if (!iRequestOppositionService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.requestopposition_exists", null, LocaleContextHolder.getLocale())));
        }
        RequestOpposition requestOpposition = iRequestOppositionService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();

        Client client;
        Users serviceClient;
        Users managerCoupon;
        ResponseRequestOppositionDTO responseRequestOppositionDTO;

        client = iClientService.getClientByInternalReference(requestOpposition.getIdClient()).get();
        serviceClient = iUserService.getByInternalReference(requestOpposition.getIdCommercialAttache());
//        managerCoupon = iUserService.getByInternalReference(requestOpposition.getIdSalesManager());
        responseRequestOppositionDTO = new ResponseRequestOppositionDTO();
        responseRequestOppositionDTO.setStatus(requestOpposition.getStatus());
        responseRequestOppositionDTO.setId(requestOpposition.getId());
        responseRequestOppositionDTO.setDescription(requestOpposition.getDescription());
        responseRequestOppositionDTO.setReason(requestOpposition.getReason());
        responseRequestOppositionDTO.setId(requestOpposition.getId());
        responseRequestOppositionDTO.setUpdateAt(requestOpposition.getUpdateAt());
        responseRequestOppositionDTO.setIdClient(client);
        responseRequestOppositionDTO.setNameClient(client.getCompleteName());
        responseRequestOppositionDTO.setIdCommercialAttache(serviceClient);
        responseRequestOppositionDTO.setNameCommercialAttache(serviceClient.getFirstName()+ "   " +serviceClient.getLastName());
//        responseRequestOppositionDTO.setIdSalesManager(managerCoupon);
//        responseRequestOppositionDTO.setNameSaleManager(managerCoupon.getFirstName()+ "   " +managerCoupon.getLastName());
        responseRequestOppositionDTO.setInternalReference(requestOpposition.getInternalReference());
        responseRequestOppositionDTO.setCreatedAt(requestOpposition.getCreatedAt());

//        return ResponseEntity.ok(responseRequestOppositionDTO);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(responseRequestOppositionDTO);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
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
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseRequestOppositionDTO> list = iRequestOppositionService.getAllRequestOppositions(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }
    }
