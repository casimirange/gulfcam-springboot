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
import com.gulfcam.fuelcoupon.order.entity.*;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.order.service.IUnitService;
import com.gulfcam.fuelcoupon.store.dto.AcceptCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.CreateCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.entity.*;
import com.gulfcam.fuelcoupon.store.repository.ICouponRepo;
import com.gulfcam.fuelcoupon.store.service.*;
import com.gulfcam.fuelcoupon.user.dto.EmailDto;
import com.gulfcam.fuelcoupon.user.entity.ETypeAccount;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    IItemService iItemService;

    @Autowired
    IUnitService iUnitService;

    @Autowired
    IClientService iClientService;

    @Autowired
    INotebookService iNotebookService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    IStationService iStationService;

    @Autowired
    ITicketService iTicketService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    ICouponRepo iCouponRepo;

    @Autowired
    IUserService iUserService;

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


        Client client = new Client();
        Notebook notebook = new Notebook();
        Station station = new Station();
        Ticket ticket = new Ticket();
        TypeVoucher typeVoucher = new TypeVoucher();

        if (createCouponDTO.getIdClient() != null) {
            if(!iClientService.getClientByInternalReference(createCouponDTO.getIdClient()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
            client = iClientService.getClientByInternalReference(createCouponDTO.getIdClient()).get();
        }

        if (createCouponDTO.getIdTypeVoucher() != null) {
            if(!iTypeVoucherService.getByInternalReference(createCouponDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typeVoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createCouponDTO.getIdTypeVoucher()).get();
        }

        if (createCouponDTO.getIdNotebook() != null) {
            if(!iNotebookService.getByInternalReference(createCouponDTO.getIdNotebook()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.notebook_exists", null, LocaleContextHolder.getLocale())));
            notebook = iNotebookService.getByInternalReference(createCouponDTO.getIdNotebook()).get();
        }

        if (createCouponDTO.getIdStation() != null) {
            if(!iStationService.getByInternalReference(createCouponDTO.getIdStation()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.station_exists", null, LocaleContextHolder.getLocale())));
            station = iStationService.getByInternalReference(createCouponDTO.getIdStation()).get();
        }

        if (createCouponDTO.getIdTicket() != null) {
            if(!iTicketService.getByInternalReference(createCouponDTO.getIdTicket()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.ticket_exists", null, LocaleContextHolder.getLocale())));
            ticket = iTicketService.getByInternalReference(createCouponDTO.getIdTicket()).get();
        }

        Coupon coupon = new Coupon();
        coupon.setInternalReference(jwtUtils.generateInternalReference());
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setSerialNumber(createCouponDTO.getSerialNumber());
        coupon.setIdClient(createCouponDTO.getIdClient());
        coupon.setIdTypeVoucher(createCouponDTO.getIdTypeVoucher());
        coupon.setIdNotebook(createCouponDTO.getIdNotebook());
        coupon.setIdStation(createCouponDTO.getIdStation());
        coupon.setIdTicket(createCouponDTO.getIdTicket());

        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        coupon.setStatus(status);

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

        if (!iCouponService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
        }
        Coupon coupon = iCouponService.getByInternalReference(internalReference).get();



        Client client = new Client();
        Notebook notebook = new Notebook();
        Station station = new Station();
        Ticket ticket = new Ticket();

        TypeVoucher typeVoucher = new TypeVoucher();
        if (createCouponDTO.getIdTypeVoucher() != null) {
            if(!iTypeVoucherService.getByInternalReference(createCouponDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typeVoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createCouponDTO.getIdTypeVoucher()).get();
        }

        if (createCouponDTO.getIdClient() != null) {
            if(!iClientService.getClientByInternalReference(createCouponDTO.getIdClient()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
            client = iClientService.getClientByInternalReference(createCouponDTO.getIdClient()).get();
        }

        if (createCouponDTO.getIdNotebook() != null) {
            if(!iNotebookService.getByInternalReference(createCouponDTO.getIdNotebook()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.notebook_exists", null, LocaleContextHolder.getLocale())));
            notebook = iNotebookService.getByInternalReference(createCouponDTO.getIdNotebook()).get();
        }

        if (createCouponDTO.getIdStation() != null) {
            if(!iStationService.getByInternalReference(createCouponDTO.getIdStation()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.station_exists", null, LocaleContextHolder.getLocale())));
            station = iStationService.getByInternalReference(createCouponDTO.getIdStation()).get();
        }

        if (createCouponDTO.getIdTicket() != null) {
            if(!iTicketService.getByInternalReference(createCouponDTO.getIdTicket()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.ticket_exists", null, LocaleContextHolder.getLocale())));
            ticket = iTicketService.getByInternalReference(createCouponDTO.getIdTicket()).get();
        }

        coupon.setUpdateAt(LocalDateTime.now());
        coupon.setSerialNumber(createCouponDTO.getSerialNumber());
        if (createCouponDTO.getIdClient() != null)
            coupon.setIdClient(createCouponDTO.getIdClient());
        if (createCouponDTO.getIdNotebook() != null)
            coupon.setIdNotebook(createCouponDTO.getIdNotebook());
        if (createCouponDTO.getIdStation() != null)
            coupon.setIdStation(createCouponDTO.getIdStation());
        if (createCouponDTO.getIdTicket() != null)
            coupon.setIdTicket(createCouponDTO.getIdTicket());
        if (createCouponDTO.getIdTypeVoucher() != null)
            coupon.setIdTypeVoucher(createCouponDTO.getIdTypeVoucher());

        iCouponService.createCoupon(coupon);

        return ResponseEntity.ok(coupon);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Client", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/client/{idClient}")
    public ResponseEntity<?> getCouponsByIdClient(@PathVariable String idClient,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                             @RequestParam(required = false, defaultValue = "idClient") String sort,
                                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {

        Page<ResponseCouponDTO> cartons = iCouponService.getCouponsByIdClient(Long.parseLong(aes.decrypt(key, idClient)),
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(cartons);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Client sous forme de fichier excel et envoyé par Mail", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/export/excel/client/{idClient}")
    public ResponseEntity<?> exportCouponsByIdClient(@PathVariable String idClient) {

        Client client = iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, idClient))).get();
        ByteArrayInputStream data = iCouponService.exportCouponsByIdClient(Long.parseLong(aes.decrypt(key, idClient)));

        Map<String, Object> emailProps2 = new HashMap<>();
        emailProps2.put("internalreference", Long.parseLong(aes.decrypt(key, idClient)));
        emailProps2.put("HttpHeaders", client.getCompleteName());
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_EXPORT_COUPONS_EXCEL, ApplicationConstant.TEMPLATE_EMAIL_EXPORT_COUPON_EXCEL, data.readAllBytes(), "export-coupons-" + aes.decrypt(key, idClient) + ".xlsx", "application/vnd.ms-excel"));
        log.info("Email send successfull for user: " + client.getEmail());


        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=export-coupons-" + aes.decrypt(key, idClient) + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));

        return ResponseEntity.ok().headers(headers).body(data.readAllBytes());
    }

    @Operation(summary = "Recupérer la liste des Coupons par Station", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/station/{idStation}")
    public ResponseEntity<?> getCouponsByIdStation(@PathVariable String idStation,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)              @RequestParam(required = false, value = "date", defaultValue = "") LocalDate date,
                                                               @RequestParam(required = false, value = "serialnumber") String serialNumber,
                                                               @RequestParam(required = false, value = "client") String clientName,
                                                               @RequestParam(required = false, value = "status") String status,
                                                               @RequestParam(required = false, value = "type") String type,
                                                                @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                @RequestParam(required = false, defaultValue = "idStation") String sort,
                                                                @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        String sn = "";
        while (sn.isEmpty()){
            sn = aes.decrypt(key, idStation);
            log.info("station décrypté "+sn);
        }
//        Coupon coupon;
//        if (iCouponService.getCouponBySerialNumber(sn).isEmpty()){
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.NOT_FOUND,
//                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
//        }
        Page<ResponseCouponDTO> cartons = iCouponService.getCouponsByIdStation(Long.parseLong(sn), date, serialNumber, clientName, status, type,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(cartons);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Carnet", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/notebook/{idNotebook}")
    public ResponseEntity<?> getCouponsByIdNotebook(@PathVariable String idNotebook,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                             @RequestParam(required = false, defaultValue = "idNotebook") String sort,
                                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {

        Page<ResponseCouponDTO> cartons = iCouponService.getCouponsByIdNotebook(Long.parseLong(aes.decrypt(key, idNotebook)),
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(cartons);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer la liste des Coupons par Ticket", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/ticket/{idTicket}")
    public ResponseEntity<?> getCouponsByIdTicket(@PathVariable String idTicket,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                             @RequestParam(required = false, defaultValue = "idTicket") String sort,
                                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {

        Page<Coupon> cartons = iCouponService.getCouponsByIdTicket(Long.parseLong(aes.decrypt(key, idTicket)),
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(cartons);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Un Coupon par sa reference interne", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getCouponByInternalReference(@PathVariable String internalReference) throws JsonProcessingException {
        Coupon coupon = iCouponService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(coupon);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Un Coupon par son Numéro de série", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<?> getCouponBySerialNumber(@PathVariable String serialNumber) throws JsonProcessingException {
        String sn = "";
        while (sn.isEmpty()){
            sn = aes.decrypt(key, serialNumber);
            log.info("coupon décrypté "+sn);
        }
        Coupon coupon;
        if (iCouponService.getCouponBySerialNumber(sn).isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
        }
        coupon = iCouponService.getCouponBySerialNumber(sn).get();

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(coupon);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Accepter Un Coupon par son Numéro de série", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @PutMapping("/accept/serial/{serialNumber}")
    public ResponseEntity<?> acceptCoupon(@Valid @RequestBody AcceptCouponDTO acceptCouponDTO, @PathVariable String serialNumber) throws JsonProcessingException {
        String sn = "";
        while (sn.isEmpty()){
            sn = aes.decrypt(key, serialNumber);
            log.info("coupon décrypté "+sn);
        }
        Coupon coupon;
        if (iCouponService.getCouponBySerialNumber(sn).isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
        }
//        if (!iCouponService.getCouponBySerialNumber(aes.decrypt(key, serialNumber)).isPresent()) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
//        }

        coupon = iCouponService.getCouponBySerialNumber(sn).get();
        Status statusCouponActived = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut du coupon:  "  +  EStatus.ACTIVATED +  "  not found"));
        if (coupon.getStatus() != statusCouponActived) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.coupon_actived_before", null, LocaleContextHolder.getLocale())));
        }

        Notebook notebook = iNotebookService.getByInternalReference(coupon.getIdNotebook()).get();
        Status status = iStatusRepo.findByName(EStatus.USED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.USED +  "  not found"));
        coupon.setStatus(status);
        coupon.setIdStation(Long.parseLong(aes.decrypt(key, acceptCouponDTO.getIdStation().toString())));
        coupon.setIdPompist(Long.parseLong(aes.decrypt(key, acceptCouponDTO.getIdPompist().toString())));
        coupon.setUpdateAt(LocalDateTime.now());
        coupon.setProductionDate(acceptCouponDTO.getProductionDate());
        coupon.setModulo(Integer.parseInt(aes.decrypt(key, acceptCouponDTO.getModulo()+"")));
        iCouponService.createCoupon(coupon);

        notebook.setStatus(status);
        notebook.setUpdateAt(LocalDateTime.now());
        iNotebookService.createNotebook(notebook);
//
//        Item item = new Item();
//        item.setIdTypeVoucher(notebook.getIdTypeVoucher());
//        item.setQuantityNotebook(-1);
//        item.setIdStoreHouse(notebook.getIdStoreHouse());
//        item.setInternalReference(jwtUtils.generateInternalReference());
//        Status statusItem = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
//        item.setStatus(statusItem);
//        item.setCreatedAt(LocalDateTime.now());
//
//        iItemService.createItem(item);

        Map<String, Object> emailProps = new HashMap<>();
        TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get();
        emailProps.put("coupon", coupon.getInternalReference()+" - "+typeVoucher.getAmount()+" FCFA - "+coupon.getSerialNumber());
        emailProps.put("station", iStationService.getByInternalReference(Long.parseLong(aes.decrypt(key, acceptCouponDTO.getIdStation()))).get());

        List<Users> usersList = iUserService.getUsers();

        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_STATION) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ACCEPT_COUPON+" #"+coupon.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_ACCEPT_COUPON));
            }
            if (user.getTypeAccount().getName() == ETypeAccount.DSI_AUDIT) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ACCEPT_COUPON+" #"+coupon.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_ACCEPT_COUPON));
            }
            if (user.getTypeAccount().getName() == ETypeAccount.COMPTABLE) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ACCEPT_COUPON+" #"+coupon.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_ACCEPT_COUPON));
            }
        }

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(coupon);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Affectation d'un carnet a un client après validation de la commande par scan d'un numéro de coupon", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @PostMapping("/affect/serial/{serialNumber}")
    public ResponseEntity<?> affectCoupon(@PathVariable String serialNumber, @RequestParam("idClient") String idClient) throws JsonProcessingException {
        String sn = "";
        String cl = "";
        while (sn.isEmpty()){
            sn = aes.decrypt(key, serialNumber);
            log.info("coupon décrypté "+sn);
        }
        while (cl.isEmpty()){
            cl = aes.decrypt(key, idClient);
            log.info("client décrypté "+cl);
        }
//        Coupon coupon;
        if (iCouponService.getCouponBySerialNumber(sn).isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
        }
//        if (!iCouponService.getCouponBySerialNumber(aes.decrypt(key, serialNumber)).isPresent()) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
//        }
        Coupon coupon = iCouponService.getCouponBySerialNumber(sn).get();
        List<Coupon> couponList = iCouponService.getCouponsByIdNotebook(coupon.getIdNotebook());
        Status status = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.ACTIVATED +  "  not found"));
        coupon.setStatus(status);
        for (Coupon item: couponList){
            item.setStatus(status);
            item.setIdClient(Long.parseLong(cl));
            item.setUpdateAt(LocalDateTime.now());
            iCouponService.createCoupon(item);
        }

        Item item = new Item();
        Unit unit = new Unit();
        Notebook notebook = iNotebookService.getByInternalReference(coupon.getIdNotebook()).get();
        Storehouse storehouse = iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get();
        TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get();

        notebook.setStatus(status);
        notebook.setUpdateAt(LocalDateTime.now());
        iNotebookService.createNotebook(notebook);

        item.setQuantityCarton(0);
        item.setIdTypeVoucher(typeVoucher.getInternalReference());
        item.setQuantityNotebook(-1);
        item.setIdStoreHouse(notebook.getIdStoreHouse());
        item.setInternalReference(jwtUtils.generateInternalReference());
        Status statusItem = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        item.setStatus(statusItem);
        item.setCreatedAt(LocalDateTime.now());

        iItemService.createItem(item);

        unit.setIdTypeVoucher(typeVoucher.getInternalReference());
        unit.setQuantityNotebook(-1);
        unit.setIdStore(storehouse.getIdStore());
        unit.setInternalReference(jwtUtils.generateInternalReference());
        Status statusUnit = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        unit.setStatus(statusUnit);
        unit.setCreatedAt(LocalDateTime.now());

        iUnitService.createUnit(unit);

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(couponList);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Supprimer un Coupon", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", description = "Coupon deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteCoupon(@PathVariable String internalReference) {
        Coupon coupon = iCouponService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        iCouponService.deleteCoupon(coupon);
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
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCouponDTO> list = iCouponService.getAllCoupons(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

//    @Parameters(value = {
//            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
//            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
//    @Operation(summary = "Liste des Coupons", tags = "Coupon", responses = {
//            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
//    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
//    @GetMapping("/filtre")
//    public ResponseEntity<?> filterCoupons(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
//                                           @RequestParam(required = false, value = "serialnumber", defaultValue = "null") String serialNumber,
//                                           @RequestParam(required = false, value = "status", defaultValue = "null") String status,
//                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
//                                           @RequestParam(required = false, defaultValue = "id") String sort,
//                                           @RequestParam(required = false, defaultValue = "desc") String order) {
//        Page<ResponseCouponDTO> list = iCouponService.filterCoupons(serialNumber, status, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
//        return ResponseEntity.ok(list);
//    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Filtrer la Liste des Coupons", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/station/notCreditNote")
    public ResponseEntity<?> getCouponByStationNotHaveCreditNote(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                           @RequestParam(required = true, value = "station") String idStation,
                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                           @RequestParam(required = false, defaultValue = "id") String sort,
                                           @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCouponDTO> list = iCouponService.couponNotHaveCreditNote(aes.decrypt(key, idStation), Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Filtrer la Liste des Coupons", tags = "Coupon", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filtrerCoupons(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                           @RequestParam(required = false, value = "serialnumber") String serialNumber,
                                           @RequestParam(required = false, value = "client") String clientName,
                                           @RequestParam(required = false, value = "status") String status,
                                           @RequestParam(required = false, value = "type") String type,
                                           @RequestParam(required = false, value = "station") String stationName,
                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                           @RequestParam(required = false, defaultValue = "id") String sort,
                                           @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCouponDTO> list = iCouponService.filtres(serialNumber, status, clientName, type, stationName, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }
    }
