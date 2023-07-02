package com.gulfcam.fuelcoupon.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.entity.ETypeClient;
import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.dto.*;
import com.gulfcam.fuelcoupon.order.entity.*;
import com.gulfcam.fuelcoupon.order.repository.IOrderRepo;
import com.gulfcam.fuelcoupon.order.repository.IStatusOrderRepo;
import com.gulfcam.fuelcoupon.order.repository.ITypeDocumentRepo;
import com.gulfcam.fuelcoupon.order.service.*;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.user.dto.EmailDto;
import com.gulfcam.fuelcoupon.user.entity.ETypeAccount;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
import com.gulfcam.fuelcoupon.user.service.IUserService;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@RestController
@Tag(name = "Order")
@RequestMapping("/api/v1.0/order")
@Slf4j
public class OrderRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    IOrderService iOrderService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IPaymentMethodService iPaymentMethodService;

    @Autowired
    IProductService iProductService;

    @Autowired
    IClientService iClientService;

    @Autowired
    IStatusOrderRepo iStatusOrderRepo;

    @Autowired
    ITypeDocumentRepo iTypeDocumentRepo;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    IOrderRepo iOrderRepo;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IDocumentStorageService iDocumentStorageService;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    ApplicationContext appContext;

    @Value("${app.numberCoupon}")
    String numberCoupon;
    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;
    @Value("${app.api.base.url}")
    private String api_base_url;

    JsonMapper jsonMapper = new JsonMapper();
    AESUtil aes = new AESUtil();
    @Value("${app.key}")
    String key;

    SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");

    @Operation(summary = "création des informations pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) throws JRException, IOException {

        Client client = new Client();
        Users salesmanager = new Users();
        Users fund = new Users();
        Users commercialAttache = new Users();
        Users managerSpaceManager2 = new Users();
        Store store = new Store();
        PaymentMethod paymentMethod = new PaymentMethod();

        if (createOrderDTO.getIdClient()  != null) {
            if(!iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdClient().toString()))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
            client = iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdClient().toString()))).get();

        }
        if (createOrderDTO.getIdSalesManager()  != null) {
            salesmanager = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdSalesManager().toString())));

            if(salesmanager.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdCommercialAttache()  != null) {
            commercialAttache = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdCommercialAttache().toString())));

            if(commercialAttache.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdSpaceManager2()  != null) {
            managerSpaceManager2 = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdSpaceManager2().toString())));

            if(managerSpaceManager2.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
//        if (createOrderDTO.getIdFund()  != null) {
//            fund = iUserService.getByInternalReference(createOrderDTO.getIdFund());
//
//            if(fund.getUserId() == null)
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
//        }
        if (createOrderDTO.getIdStore()  != null) {
            if(!iStoreService.getByInternalReference(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdStore().toString()))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store = iStoreService.getByInternalReference(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdStore().toString()))).get();

        }
//        if (createOrderDTO.getIdPaymentMethod()  != null) {
//            if(!iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).isPresent())
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.payment_exists", null, LocaleContextHolder.getLocale())));
//            paymentMethod = iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).get();
//
//        }

        Long internalReference = jwtUtils.generateInternalReference();
        Order order = new Order();
        order.setInternalReference(internalReference);
        order.setCreatedAt(LocalDateTime.now());
        order.setClientReference(createOrderDTO.getClientReference() != null ? aes.decrypt(key, createOrderDTO.getClientReference()) : "");
        order.setIdClient(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdClient().toString())));
//        order.setIdFund(createOrderDTO.getIdFund());
//        order.setIdSalesManager(createOrderDTO.getIdSalesManager());
        order.setIdCommercialAttache(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdCommercialAttache().toString())));
        order.setChannel(aes.decrypt(key, createOrderDTO.getChannel()));
        order.setDescription(createOrderDTO.getDescription() != null ? aes.decrypt(key, createOrderDTO.getDescription()) :"");
        order.setDeliveryTime(createOrderDTO.getDeliveryTime() != null ? aes.decrypt(key, createOrderDTO.getDeliveryTime()) :"");
//        order.setIdPaymentMethod(createOrderDTO.getIdPaymentMethod());
        order.setNetAggregateAmount(Integer.parseInt(aes.decrypt(key, createOrderDTO.getNetAggregateAmount()+"")));
        order.setTTCAggregateAmount(Integer.parseInt(aes.decrypt(key, createOrderDTO.getTTCAggregateAmount()+"")));
        order.setTax(aes.decrypt(key, createOrderDTO.getTax()));
//        order.setIdSpaceManager2(createOrderDTO.getIdSpaceManager2());
        order.setIdStore(Long.parseLong(aes.decrypt(key, createOrderDTO.getIdStore().toString())));
        order.setPaymentReference(createOrderDTO.getPaymentReference());

        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.CREATED +  "  not found"));
        order.setStatus(statusOrder);

        List<Users> usersList = iUserService.getUsers();

        String emailToTresury = "";
        String emailToStore = "";

        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.TREASURY) {
                emailToTresury = user.getEmail();
            }
        }

        if(createOrderDTO.getIdCommercialAttache() != null){
            emailToStore += commercialAttache.getEmail();
        }
        String ClientReference = createOrderDTO.getIdClient()+" - "+client.getEmail()+" - "+client.getCompleteName()+" - "+client.getCompanyName();
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", internalReference);
        emailProps.put("internalReferenceClient", ClientReference);
        emailProps.put("internalReferenceStore", aes.decrypt(key, createOrderDTO.getIdStore()));
        emailProps.put("delivryTime", createOrderDTO.getDeliveryTime() != null ? aes.decrypt(key, createOrderDTO.getDeliveryTime()) :"");
        emailProps.put("canal",aes.decrypt(key,  createOrderDTO.getChannel()));
        emailProps.put("netAmount", aes.decrypt(key, createOrderDTO.getNetAggregateAmount()));
        emailProps.put("ttcAmount", aes.decrypt(key, createOrderDTO.getTTCAggregateAmount()));
        emailProps.put("payementMethode", (createOrderDTO.getIdPaymentMethod() == null)? "":createOrderDTO.getIdPaymentMethod()+ " - "+paymentMethod.getDesignation());

        if(emailToTresury != null){
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToTresury, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_ORDER+ internalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_ORDER));
            log.info("Email  send successfull for user: " + emailToTresury);
        }

        if(emailToStore != null){
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToStore, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_ORDER+ internalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_ORDER));
            log.info("Email  send successfull for user: " + emailToStore);
        }

        iOrderService.createOrder(order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(order);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Modification des informations pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{InternalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO, @PathVariable Long InternalReference) throws JsonProcessingException {

//        if (!iOrderService.getByInternalReference(InternalReference).isPresent()) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.order_exists", null, LocaleContextHolder.getLocale())));
//        }
//        Order order = iOrderService.getByInternalReference(InternalReference).get();
//        Client client = new Client();
//        Users salesmanager = new Users();
//        Users fund = new Users();
//        Users commercialAttache = new Users();
//        Users managerSpaceManager2 = new Users();
//        Store store = new Store();
//        PaymentMethod paymentMethod = new PaymentMethod();
//
//        if (createOrderDTO.getIdClient()  != null) {
//            if(!iClientService.getClientByInternalReference(createOrderDTO.getIdClient()).isPresent())
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
//            client = iClientService.getClientByInternalReference(createOrderDTO.getIdClient()).get();
//
//        }
//        if (createOrderDTO.getIdSalesManager()  != null) {
//            salesmanager = iUserService.getByInternalReference(createOrderDTO.getIdSalesManager());
//
//            if(salesmanager.getUserId() == null)
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
//        }
//        if (createOrderDTO.getIdCommercialAttache()  != null) {
//            commercialAttache = iUserService.getByInternalReference(createOrderDTO.getIdCommercialAttache());
//
//            if(commercialAttache.getUserId() == null)
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
//        }
//        if (createOrderDTO.getIdSpaceManager2()  != null) {
//            managerSpaceManager2 = iUserService.getByInternalReference(createOrderDTO.getIdSpaceManager2());
//
//            if(managerSpaceManager2.getUserId() == null)
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
//        }
//        if (createOrderDTO.getIdFund()  != null) {
//            fund = iUserService.getByInternalReference(createOrderDTO.getIdFund());
//
//            if(fund.getUserId() == null)
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
//        }
//        if (createOrderDTO.getIdStore()  != null) {
//            if(!iStoreService.getByInternalReference(createOrderDTO.getIdStore()).isPresent())
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
//            store = iStoreService.getByInternalReference(createOrderDTO.getIdStore()).get();
//
//        }
//        if (createOrderDTO.getIdPaymentMethod()  != null) {
//            if(!iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).isPresent())
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.payment_exists", null, LocaleContextHolder.getLocale())));
//            paymentMethod = iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).get();
//
//        }
//        order.setUpdateAt(LocalDateTime.now());
//        order.setClientReference(createOrderDTO.getClientReference());
//        if (createOrderDTO.getIdClient() != null)
//            order.setIdClient(createOrderDTO.getIdClient());
//        if (createOrderDTO.getIdFund() != null)
//            order.setIdFund(createOrderDTO.getIdFund());
//        if (createOrderDTO.getIdSalesManager() != null)
//            order.setIdSalesManager(createOrderDTO.getIdSalesManager());
//        if (createOrderDTO.getIdCommercialAttache() != null)
//            order.setIdCommercialAttache(createOrderDTO.getIdCommercialAttache());
//        order.setChannel(createOrderDTO.getChannel());
//        order.setDescription(createOrderDTO.getDescription());
//        order.setDeliveryTime(createOrderDTO.getDeliveryTime());
//        if (createOrderDTO.getIdPaymentMethod() != null)
//            order.setIdPaymentMethod(createOrderDTO.getIdPaymentMethod());
//        order.setNetAggregateAmount(createOrderDTO.getNetAggregateAmount());
//        order.setTTCAggregateAmount(createOrderDTO.getTTCAggregateAmount());
//        order.setTax(createOrderDTO.getTax());
//        if (createOrderDTO.getIdSpaceManager2() != null)
//            order.setIdSpaceManager2(createOrderDTO.getIdSpaceManager2());
//        if (createOrderDTO.getIdStore() != null)
//            order.setIdStore(createOrderDTO.getIdStore());
//        order.setPaymentReference(createOrderDTO.getPaymentReference());
//
//        iOrderService.createOrder(order);
//
//        jsonMapper.registerModule(new JavaTimeModule());
//        Object json = jsonMapper.writeValueAsString(order);
//        JSONObject cr = aes.encryptObject( key, json);
//        return ResponseEntity.ok(cr);
        return null;
    }

    @Operation(summary = "Effectuer un paiement pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/accept/{InternalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> acceptOrder(@RequestBody MultipartFile file, @PathVariable String InternalReference, @RequestParam("idFund") String idFund, @RequestParam("idPaymentMethod") String idPaymentMethod, @RequestParam("paymentReference") String paymentReference, @RequestParam("docType") String docType) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(Long.parseLong(aes.decrypt(key, InternalReference))).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        StatusOrder statusOrderCreated = iStatusOrderRepo.findByName(EStatusOrder.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.CREATED +  "  not found"));
        if (order.getStatus() != statusOrderCreated) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_created_before", null, LocaleContextHolder.getLocale())));
        }
        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.ACCEPTED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.ACCEPTED +  "  not found"));
        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.INVOICE).orElseThrow(()-> new ResourceNotFoundException("Statut :  "  +  ETypeDocument.INVOICE +  "  not found"));
        String fileName = iDocumentStorageService.storeFile(file, Long.parseLong(aes.decrypt(key, InternalReference)), docType, typeDocument);
        String fileDownloadUri = api_base_url+"/api/v1.0/order/file/" + aes.decrypt(key, InternalReference) + "/downloadFile?type=invoice&docType=" + docType;
        order.setLinkInvoice(fileDownloadUri);
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdPaymentMethod(Long.parseLong(aes.decrypt(key, idPaymentMethod)));
        order.setIdFund(Long.parseLong(aes.decrypt(key, idFund)));
        order.setPaymentReference(paymentReference);
        iOrderService.createOrder(order);

        byte[] data = generateReceived(order, client);
//
//        Map<String, Object> emailProps2 = new HashMap<>();
//        emailProps2.put("Internalreference", InternalReference);
//        emailProps2.put("completename", client.getCompleteName());
//        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_NEW_RECEIVED+InternalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_RECEIVED, data));
//        log.info("Email send successfull for user: " + client.getEmail());

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", Long.parseLong(aes.decrypt(key, InternalReference)));
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.ACCEPTED);
        emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        List<Users> usersList = iUserService.getUsersByIdStore(order.getIdStore());
        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.TREASURY) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+Long.parseLong(aes.decrypt(key, InternalReference))+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }

            if (user.getTypeAccount().getName() == ETypeAccount.SALES_MANAGER) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+Long.parseLong(aes.decrypt(key, InternalReference))+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=received-" + order.getClientReference() + ".pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

    @Operation(summary = "Générer le bordereau de livraison pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/delivery/{internalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> devliveryOrder(@PathVariable String internalReference, @RequestParam("idSalesManager") String idSalesManager) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        StatusOrder statusOrderPaid = iStatusOrderRepo.findByName(EStatusOrder.PAID).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.PAID +  "  not found"));
        if (order.getStatus() != statusOrderPaid) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_pay_before", null, LocaleContextHolder.getLocale())));
        }
        byte[] data = generateDelivery(order, client);
        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.IN_PROCESS_OF_DELIVERY).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.IN_PROCESS_OF_DELIVERY +  "  not found"));
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdSalesManager(Long.parseLong(aes.decrypt(key, idSalesManager)));
        iOrderService.createOrder(order);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=delivery-" + order.getClientReference() + ".pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

//    @Operation(summary = "Emettre proformat ou pré-facture pour une commande", tags = "Order", responses = {
//            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
//            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
//    @PostMapping("/invoice/{InternalReference:[0-9]+}")
//    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
//    public ResponseEntity<?> invoiceOrder(@PathVariable Long internalReference) throws JRException, IOException {
//
//        Order order = iOrderService.getByInternalReference(internalReference).get();
//        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
//        StatusOrder statusOrderPaid = iStatusOrderRepo.findByName(EStatusOrder.PAID).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.PAID +  "  not found"));
//        if (order.getStatus() != statusOrderPaid) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.order_pay_before", null, LocaleContextHolder.getLocale())));
//        }
//        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());
//        if (products.isEmpty()) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.noproduct", null, LocaleContextHolder.getLocale())));
//        }
//
//        byte[] data = generateInvoice(order, client);
//
//        Map<String, Object> emailProps2 = new HashMap<>();
//        emailProps2.put("Internalreference", internalReference);
//        emailProps2.put("completename", client.getCompleteName());
//        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_NEW_INVOICE+internalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_INVOICE, data));
//        log.info("Email send successfull for user: " + client.getEmail());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=invoice-" + order.getClientReference() + ".pdf");
//
//        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//    }

    @Operation(summary = "Télécharger un document (Bon de commande, reçue ou preuve de paiement) pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = "Application/Json")),})
    @GetMapping("/file/{InternalReference}/downloadFile")
    public ResponseEntity<Object> downloadFile(@PathVariable("InternalReference") String InternalReference, @Schema(required = true, allowableValues = {"INVOICE", "DELIVERY"}, description = "Type de document") @RequestParam("type") String type, @RequestParam("docType") String docType,
                                               HttpServletRequest request) throws JsonProcessingException {

        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.valueOf(type.toUpperCase())).orElseThrow(()-> new ResourceNotFoundException("Type de document:  "  +  type +  "  not found"));

        String fileName = iDocumentStorageService.getDocumentName(Long.parseLong(aes.decrypt(key, InternalReference)), docType, typeDocument.getId());
        Resource resource = null;
        if (fileName != null && !fileName.isEmpty()) {
            try {
                resource = iDocumentStorageService.loadFileAsResource(fileName);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException e) {
                log.info("Could not determine file type.");
            }
            // Fallback to the default content type if type could not be determined
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
//            jsonMapper.registerModule(new JavaTimeModule());
//            Object json = jsonMapper.writeValueAsString(resource);
//            JSONObject cr = aes.encryptObject( key, json);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
    }

    @Operation(summary = "Valider le paiement pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/pay/{InternalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> payOrder(@PathVariable String InternalReference, @RequestParam("idSalesManager") String idSalesManager) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(Long.parseLong(aes.decrypt(key, InternalReference))).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        StatusOrder statusOrderAccept = iStatusOrderRepo.findByName(EStatusOrder.ACCEPTED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.ACCEPTED +  "  not found"));
        if (order.getStatus() != statusOrderAccept) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_accept_before", null, LocaleContextHolder.getLocale())));
        }
        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.PAID).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.PAID +  "  not found"));
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdSalesManager(Long.parseLong(aes.decrypt(key, idSalesManager)));
        iOrderService.createOrder(order);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", Long.parseLong(aes.decrypt(key, InternalReference)));
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.PAID);
        emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        List<Users> usersList = iUserService.getUsersByIdStore(order.getIdStore());
        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_SPACES_2) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+Long.parseLong(aes.decrypt(key, InternalReference))+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }

            if (user.getTypeAccount().getName() == ETypeAccount.SALES_MANAGER) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+Long.parseLong(aes.decrypt(key, InternalReference))+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }
        }
        if(client.getEmail() != null){
            byte[] data = generateReceived(order, client);

            Map<String, Object> emailProps2 = new HashMap<>();
            emailProps2.put("Internalreference", Long.parseLong(aes.decrypt(key, InternalReference)));
            emailProps2.put("completename", client.getCompleteName());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_NEW_RECEIVED+Long.parseLong(aes.decrypt(key, InternalReference)), ApplicationConstant.TEMPLATE_EMAIL_NEW_RECEIVED, data));
            log.info("Email send successfull for user: " + client.getEmail());
        }
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(order);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Annuler une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/cancel/{InternalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> cancelOrder(@PathVariable String InternalReference, @RequestParam("idCommercialAttache") String idCommercialAttache, @RequestParam("reasonForCancellation")  @Schema(description = "Raison d’annulation") String reasonForCancellation) throws JsonProcessingException {

        Order order = iOrderService.getByInternalReference(Long.parseLong(aes.decrypt(key, InternalReference))).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();

        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.CANCELED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.CANCELED +  "  not found"));
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdCommercialAttache(Long.parseLong(aes.decrypt(key, idCommercialAttache)));
        order.setReasonForCancellation(reasonForCancellation);
        iOrderService.createOrder(order);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", Long.parseLong(aes.decrypt(key, InternalReference)));
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.CANCELED);
        emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        Users commercialAttache = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, idCommercialAttache)));
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, commercialAttache.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_ORDER+Long.parseLong(aes.decrypt(key, InternalReference))+" - "+EStatusOrder.CANCELED, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_ORDER));
        log.info("Email  send successfull for user: " + commercialAttache.getEmail());

        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_ORDER+Long.parseLong(aes.decrypt(key, InternalReference))+" - "+EStatusOrder.CANCELED, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_ORDER));
        log.info("Email  send successfull for user: " + client.getEmail());
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(order);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Annuler plusieurs commandes", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/cancel/multi")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> cancelMultiOrder(@Valid @RequestBody CancelMultiOrderDTO cancelMultiOrderDTO) {

        for(int i=0; i<= cancelMultiOrderDTO.getOrders().size(); i++){
            Order order = iOrderService.getByInternalReference(cancelMultiOrderDTO.getOrders().get(i)).get();
            Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();

            StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.CANCELED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.CANCELED +  "  not found"));
            order.setStatus(statusOrder);
            order.setUpdateAt(LocalDateTime.now());
            iOrderService.createOrder(order);

            Map<String, Object> emailProps = new HashMap<>();
            emailProps.put("internalReferenceOrder", cancelMultiOrderDTO.getOrders().get(i));
            emailProps.put("internalReferenceClient", order.getClientReference());
            emailProps.put("internalReferenceStore", order.getIdStore());
            emailProps.put("delivryTime", order.getDeliveryTime());
            emailProps.put("canal", order.getChannel());
            emailProps.put("netAmount", order.getNetAggregateAmount());
            emailProps.put("ttcAmount", order.getTTCAggregateAmount());
            emailProps.put("status", EStatusOrder.CANCELED);
            emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_MULTI_ORDER+cancelMultiOrderDTO.getOrders().get(i)+" - "+EStatusOrder.CANCELED, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_ORDER));
            log.info("Email  send successfull for user: " + client.getEmail());

        }

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("order", cancelMultiOrderDTO.getOrders());
        Users commercialAttache = iUserService.getByInternalReference(cancelMultiOrderDTO.getIdCommercialAttache());
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, commercialAttache.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_MULTI_ORDER+" - "+EStatusOrder.CANCELED, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_MULTI_ORDER));
        log.info("Email  send successfull for user: " + commercialAttache.getEmail());

        return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK, " Les commandes ont été annulé avec succèss !"));
    }

    @Operation(summary = "valider le bon de livraison et terminer pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/valid/delivery/{InternalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> validDeliveryOrder(@RequestBody MultipartFile file, @PathVariable String InternalReference, @RequestParam("idSalesManager") String idSalesManager) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(Long.parseLong(aes.decrypt(key, InternalReference))).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        StatusOrder statusOrderDelivery = iStatusOrderRepo.findByName(EStatusOrder.IN_PROCESS_OF_DELIVERY).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.IN_PROCESS_OF_DELIVERY +  "  not found"));
        if (order.getStatus() != statusOrderDelivery) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_delivery_before", null, LocaleContextHolder.getLocale())));
        }
        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.CLOSED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.CLOSED +  "  not found"));
        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.DELIVERY).orElseThrow(()-> new ResourceNotFoundException("Statut :  "  +  ETypeDocument.DELIVERY +  "  not found"));
        String fileName = iDocumentStorageService.storeFile(file, Long.parseLong(aes.decrypt(key, InternalReference)), "pdf", typeDocument);
        String fileDownloadUri = api_base_url+"/api/v1.0/order/file/" + Long.parseLong(aes.decrypt(key, InternalReference)) + "/downloadFile?type=delivery&docType=pdf";
        order.setLinkDelivery(fileDownloadUri);
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdSalesManager(Long.parseLong(aes.decrypt(key, idSalesManager)));
        iOrderService.createOrder(order);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", Long.parseLong(aes.decrypt(key, InternalReference)));
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.CLOSED);
        emailProps.put("payementMethode", order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        List<Users> usersList = iUserService.getUsersByIdStore(order.getIdStore());
        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.SALES_MANAGER) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+Long.parseLong(aes.decrypt(key, InternalReference))+" - "+EStatusOrder.CLOSED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }
        }
        if(client.getEmail() != null){
            byte[] data = generateFacture(order, client);

            Map<String, Object> emailProps2 = new HashMap<>();
            emailProps2.put("Internalreference", Long.parseLong(aes.decrypt(key, InternalReference)));
            emailProps2.put("completename", client.getCompleteName());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_NEW_FACTURE+Long.parseLong(aes.decrypt(key, InternalReference)), ApplicationConstant.TEMPLATE_EMAIL_NEW_FACTURE, data));
            log.info("Email send successfull for user: " + client.getEmail());
        }

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(order);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer la liste des commandes par client sous forme de fichier excel et envoyé par Mail", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/export/excel/client/{idClient}")
    public ResponseEntity<?> exportOrdersByIdClient(@PathVariable String idClient) {
        Client client = iClientService.getClientByInternalReference(Long.parseLong(aes.decrypt(key, idClient))).get();
        ByteArrayInputStream data = iOrderService.exportOrdersByIdClient(Long.parseLong(aes.decrypt(key, idClient)));

        Map<String, Object> emailProps2 = new HashMap<>();
        emailProps2.put("Internalreference", Long.parseLong(aes.decrypt(key, idClient)));
        emailProps2.put("CompleteName", client.getCompleteName());
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_EXPORT_ORDERS_EXCEL, ApplicationConstant.TEMPLATE_EMAIL_EXPORT_ORDER_EXCEL, data.readAllBytes(),"export-commandes-" + aes.decrypt(key, idClient) + ".xlsx", "application/vnd.ms-excel"));
        log.info("Email send successfull for user: " + client.getEmail());


        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=export-commandes-" + aes.decrypt(key, idClient) + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));

        return ResponseEntity.ok().headers(headers).body(data.readAllBytes());
    }

    @Operation(summary = "Recupérer la liste des commandes par client", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/client/{idClient}")
    public ResponseEntity<?> getOrdersByIdClient(@PathVariable String idClient,
                                                           @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                           @RequestParam(required = false, defaultValue = "idClient") String sort,
                                                           @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {

        Page<ResponseOrderDTO> orders = iOrderService.getOrdersByIdClient(Long.parseLong(aes.decrypt(key, idClient)),
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(orders);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer la liste des commandes par caisse/trésorerie", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/fund/{idFund:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdFund(@PathVariable Long idFund,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idFund") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdFund(idFund,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }


    @Operation(summary = "Recupérer la liste des commandes par Directeur commercial", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/salesmanager/{idSalesManager:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdsalesmanager(@PathVariable Long idSalesManager,
                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                 @RequestParam(required = false, defaultValue = "idsalesmanager") String sort,
                                                                 @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdSalesManager(idSalesManager,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Recupérer la liste des commandes par Gestionnaire espace 2", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/spacemanager2/{idSpaceManager2:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdSpaceManager2(@PathVariable Long idSpaceManager2,
                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                 @RequestParam(required = false, defaultValue = "idsalesmanager") String sort,
                                                                 @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdSpaceManager2(idSpaceManager2,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Recupérer la liste des commandes par attaché commercial", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/commercialattache/{idCommercialAttache:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdCommercialAttache(@PathVariable Long idCommercialAttache,
                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                 @RequestParam(required = false, defaultValue = "idsalesmanager") String sort,
                                                                 @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdCommercialAttache(idCommercialAttache,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }

//    @Operation(summary = "Recupérer la liste des commandes par gestionnaire de commandes", tags = "Order", responses = {
//            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
//            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
//    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
//    @GetMapping("/managerstore/{idManagerOrder:[0-9]+}")
//    public ResponseEntity<Page<Order>> getOrdersByIdManagerStore(@PathVariable Long idManagerOrder,
//                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
//                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
//                                                                 @RequestParam(required = false, defaultValue = "idManagerOrder") String sort,
//                                                                 @RequestParam(required = false, defaultValue = "desc") String order) {
//
//        Page<Order> orders = iOrderService.getOrdersByIdManagerOrder(idManagerOrder,
//                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
//        return ResponseEntity.ok(orders);
//    }


//    @Operation(summary = "Recupérer la liste des commandes par magasinier", tags = "Order", responses = {
//            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
//            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
//    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
//    @GetMapping("/storekeeper/{idStorekeeper:[0-9]+}")
//    public ResponseEntity<Page<Order>> getOrdersByIdStorekeeper(@PathVariable Long idStorekeeper,
//                                                                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
//                                                                  @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
//                                                                  @RequestParam(required = false, defaultValue = "idStorekeeper") String sort,
//                                                                  @RequestParam(required = false, defaultValue = "desc") String order) {
//
//        Page<Order> orders = iOrderService.getOrdersByIdStorekeeper(idStorekeeper,
//                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
//        return ResponseEntity.ok(orders);
//    }

    @Operation(summary = "Recupérer la liste des commandes par magasin", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/store/{idStore:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdStore(@PathVariable Long idStore,
                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                 @RequestParam(required = false, defaultValue = "idStore") String sort,
                                                                 @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdStore(idStore,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Filtrer les commandes", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filterOrders(@RequestParam(required = false, value = "store" ) String idStore,
                                                               @RequestParam(required = false, value = "client" ) String clientName,
     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)            @RequestParam(required = false, value = "date" ) LocalDate date,
                                                               @RequestParam(required = false, value = "ref" ) String internalRef,
                                                               @RequestParam(required = false, value = "status") String status,
                                                               @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                               @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                               @RequestParam(required = false, defaultValue = "id") String sort,
                                                               @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {

        Page<ResponseOrderDTO> orders = iOrderService.filtrerOrders(idStore, clientName, date, internalRef, status,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(orders);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Une commande par son Identifiant interne", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getOrderByInternalReference(@PathVariable String internalReference) throws JsonProcessingException {
        log.info("crypté "+ internalReference);
        log.info("décrypté "+ aes.decrypt(key, internalReference));
        ResponseOrderDTO orders = iOrderService.getOrderByInternalReference(Long.parseLong(aes.decrypt(key, internalReference)));
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(orders);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }


    @Operation(summary = "La proforma numérique ou la préfacture numérique générée pour une commande par son Identifiant interne", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/invoice/{internalReference}")
    public ResponseEntity<?> generateInvoiceByInternalReference(@PathVariable String internalReference) throws JRException, IOException {
        Order order = iOrderService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        byte[] data = generateInvoice(order, client);
        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());
        if (products.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.noproduct", null, LocaleContextHolder.getLocale())));
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.ADMINISTRATION)){
            testTypeDocument = true;
        }
        Map<String, Object> emailProps2 = new HashMap<>();
        emailProps2.put("Internalreference", Long.parseLong(aes.decrypt(key, internalReference)));
        emailProps2.put("TypeDocument", testTypeDocument? "PREFACTURE":"PROFORMA");
        emailProps2.put("CompleteName", client.getCompleteName());
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, testTypeDocument ? ApplicationConstant.SUBJECT_EMAIL_NEW_INVOICE2+internalReference: ApplicationConstant.SUBJECT_EMAIL_NEW_INVOICE+internalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_INVOICE, data));
        log.info("Email send successfull for user: " + client.getEmail());


        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=invoice-" + order.getClientReference() + ".pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

    @Operation(summary = "le reçu de paiement et le bon de livraison numérique générée pour une commande par son Identifiant interne", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @PostMapping("/document/{internalReference}")
    public ResponseEntity<?> generateInvoiceOrDelivery(@PathVariable String internalReference, @Schema(required = true, allowableValues = {"INVOICE", "DELIVERY"}, description = "Type de document") @RequestParam("type") String type) throws JRException, IOException {
        Order order = iOrderService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();

        byte[] data;
        HttpHeaders headers = new HttpHeaders();

        if(type.equalsIgnoreCase("DELIVERY")){
            data = generateDelivery(order, client);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=delivery-" + order.getInternalReference() + ".pdf");
        }else{
            data = generateReceived(order, client);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=receive-" + order.getInternalReference() + ".pdf");
        }

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

    @Operation(summary = "Supprimer une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", description = "Client deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteOrder(@PathVariable Long internalReference) {
        Order order = iOrderService.getByInternalReference(internalReference).get();
        iOrderService.deleteOrder(order);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "createdAt", description = "Filtrer la liste par date de création"),
            @Parameter(name = "status", description = "Filtrer la liste par le status"),
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des commandes", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getOrders(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                       @RequestParam(required = false, defaultValue = "id") String sort,
                                       @RequestParam(required = false, defaultValue = "id") String createdAt,
                                       @RequestParam(required = false, defaultValue = "id") String status,
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws IOException {
        Page<ResponseOrderDTO> list = iOrderService.getAllOrders(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order, createdAt, status);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    private byte[] generateInvoice(Order order, Client client) throws JRException, IOException {
        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());

        ProductDTO productDTO = new ProductDTO();
        List<ProductDTO> productDTOList = new ArrayList<>();

        for(Product product : products){
            TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get();
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(Math.round(typeVoucher.getAmount())*Integer.parseInt(numberCoupon)+"");
            productDTO.setValeur(Integer.parseInt(productDTO.getPu())*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+Math.round(typeVoucher.getAmount()));
            productDTOList.add(productDTO);
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.ADMINISTRATION)){
            testTypeDocument = true;
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("typeDocument", testTypeDocument? "PREFACTURE":"PROFORMA");
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", Float.valueOf(order.getTax())*10+"");
        parameters.put("TTCAggregateAmount", order.getTTCAggregateAmount()+"");
        parameters.put("completeName", client.getCompleteName());
        parameters.put("dateOrder", dateFor.format(Date.from(order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("address", (client.getAddress() != null) ? client.getAddress(): " - ");
        parameters.put("phone", (client.getPhone() != null)? client.getPhone()+"" : " - ");
        parameters.put("email", (client.getEmail() != null)? client.getEmail(): " - ");
        parameters.put("rc", (client.getRCCM() != null)? client.getRCCM()+"": " - ");
        parameters.put("clientReference",  (order.getClientReference() == null) ? client.getInternalReference()+"": order.getClientReference()+"");
        parameters.put("idcommand", order.getInternalReference()+"");
        parameters.put("the_date", dateFor.format(new Date()).toString());
        Resource resourceLogo = appContext.getResource("classpath:/templates/logo.jpeg");
        //Compile to jasperReport
        InputStream inputStreamLogo = resourceLogo.getInputStream();
        parameters.put("logo", inputStreamLogo);
        /* read jrxl fille and creat jasperdesign object*/
        Resource resource = appContext.getResource("classpath:/templates/jasper/proforma.jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        /* compiling jrxml with help of JasperReport class*/
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Using jasperReport objet to generate PDF*/
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        /*convert PDF to byte type*/
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private byte[] generateReceived(Order order, Client client) throws JRException, IOException {
        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());

        ProductDTO productDTO = new ProductDTO();
        List<ProductDTO> productDTOList = new ArrayList<>();

        for(Product product : products){
            TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get();
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(Math.round(typeVoucher.getAmount())*Integer.parseInt(numberCoupon)+"");
            productDTO.setValeur(Integer.parseInt(productDTO.getPu())*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+Math.round(typeVoucher.getAmount()));
            productDTOList.add(productDTO);
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.ADMINISTRATION)){
            testTypeDocument = true;
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("type", testTypeDocument? "PREFACTURE":"PROFORMA");
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", Float.valueOf(order.getTax())*10+"");
        parameters.put("dateOrder", dateFor.format(Date.from((order.getUpdateAt() == null) ? order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  order.getUpdateAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("TTCAggregateAmount", order.getTTCAggregateAmount()+"");
        parameters.put("completeName", client.getCompleteName());
        parameters.put("companyName", client.getCompanyName());
        parameters.put("address", (client.getAddress() != null) ? client.getAddress(): " - ");
        parameters.put("phone", (client.getPhone() != null)? client.getPhone()+"" : " - ");
        parameters.put("email", (client.getEmail() != null)? client.getEmail(): " - ");
        parameters.put("rc", (client.getRCCM() != null)? client.getRCCM()+"": " - ");
        parameters.put("clientReference", (order.getClientReference() == null) ? client.getInternalReference()+"": order.getClientReference() +"");
        parameters.put("idcommand", order.getInternalReference()+"");
        parameters.put("the_date", dateFor.format(new Date()).toString());
        Resource resourceLogo = appContext.getResource("classpath:/templates/logo.jpeg");
        //Compile to jasperReport
        InputStream inputStreamLogo = resourceLogo.getInputStream();
        parameters.put("logo", inputStreamLogo);
        /* read jrxl fille and creat jasperdesign object*/
        Resource resource = appContext.getResource("classpath:/templates/jasper/received.jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        /* compiling jrxml with help of JasperReport class*/
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Using jasperReport objet to generate PDF*/
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        /*convert PDF to byte type*/
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private byte[] generateFacture(Order order, Client client) throws JRException, IOException {
        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());

        ProductDTO productDTO = new ProductDTO();
        List<ProductDTO> productDTOList = new ArrayList<>();

        for(Product product : products){
            TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get();
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(Math.round(typeVoucher.getAmount())*Integer.parseInt(numberCoupon)+"");
            productDTO.setValeur(Integer.parseInt(productDTO.getPu())*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+Math.round(typeVoucher.getAmount()));
            productDTOList.add(productDTO);
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.ADMINISTRATION)){
            testTypeDocument = true;
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("type", testTypeDocument? "PREFACTURE":"PROFORMA");
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", Float.valueOf(order.getTax())*10+"");
        parameters.put("dateOrder", dateFor.format(Date.from((order.getUpdateAt() == null) ? order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  order.getUpdateAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("TTCAggregateAmount", order.getTTCAggregateAmount()+"");
        parameters.put("completeName", client.getCompleteName());
        parameters.put("companyName", client.getCompanyName());
        parameters.put("address", (client.getAddress() != null) ? client.getAddress(): " - ");
        parameters.put("phone", (client.getPhone() != null)? client.getPhone()+"" : " - ");
        parameters.put("email", (client.getEmail() != null)? client.getEmail(): " - ");
        parameters.put("rc", (client.getRCCM() != null)? client.getRCCM()+"": " - ");
        parameters.put("clientReference", (order.getClientReference() == null) ? client.getInternalReference()+"": order.getClientReference() +"");
        parameters.put("idcommand", order.getInternalReference()+"");
        parameters.put("the_date", dateFor.format(new Date()).toString());
        Resource resourceLogo = appContext.getResource("classpath:/templates/logo.jpeg");
        //Compile to jasperReport
        InputStream inputStreamLogo = resourceLogo.getInputStream();
        parameters.put("logo", inputStreamLogo);
        /* read jrxl fille and creat jasperdesign object*/
        Resource resource = appContext.getResource("classpath:/templates/jasper/invoice.jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        /* compiling jrxml with help of JasperReport class*/
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Using jasperReport objet to generate PDF*/
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        /*convert PDF to byte type*/
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private byte[] generateDelivery(Order order, Client client) throws JRException, IOException {
        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());

        ProductDTO productDTO = new ProductDTO();
        List<ProductDTO> productDTOList = new ArrayList<>();

        for(Product product : products){
            TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get();
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(Math.round(typeVoucher.getAmount())*Integer.parseInt(numberCoupon)+"");
            productDTO.setValeur(Integer.parseInt(productDTO.getPu())*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+Math.round(typeVoucher.getAmount()));
            productDTOList.add(productDTO);
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", Float.valueOf(order.getTax())*10+"");
        parameters.put("dateOrder", dateFor.format(Date.from((order.getUpdateAt() == null) ? order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  order.getUpdateAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("TTCAggregateAmount", order.getTTCAggregateAmount()+"");
        parameters.put("completeName", client.getCompleteName());
        parameters.put("companyName", client.getCompanyName());
        parameters.put("address", (client.getAddress() != null) ? client.getAddress(): " - ");
        parameters.put("phone", (client.getPhone() != null)? client.getPhone()+"" : " - ");
        parameters.put("email", (client.getEmail() != null)? client.getEmail(): " - ");
        parameters.put("rc", (client.getRCCM() != null)? client.getRCCM()+"": " - ");
        parameters.put("clientReference", (order.getClientReference() == null) ? client.getInternalReference()+"": order.getClientReference()+"");
        parameters.put("idcommand", order.getInternalReference()+"");
        parameters.put("the_date", dateFor.format(new Date()).toString());
        Resource resourceLogo = appContext.getResource("classpath:/templates/logo.jpeg");
        //Compile to jasperReport
        InputStream inputStreamLogo = resourceLogo.getInputStream();
        parameters.put("logo", inputStreamLogo);
//        parameters.put("logo", ResourceUtils.getFile("classpath:/templates/logo.jpeg").getAbsolutePath());
        /* read jrxl fille and creat jasperdesign object*/

        Resource resource = appContext.getResource("classpath:/templates/jasper/delivery.jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        /* compiling jrxml with help of JasperReport class*/
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Using jasperReport objet to generate PDF*/
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        /*convert PDF to byte type*/
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
