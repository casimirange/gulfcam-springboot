package com.gulfcam.fuelcoupon.order.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.entity.ETypeClient;
import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import com.gulfcam.fuelcoupon.client.service.IClientService;
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
import java.time.LocalDateTime;
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

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;
    @Value("${app.api.base.url}")
    private String api_base_url;

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
        Users managerCoupon = new Users();
        Users fund = new Users();
        Users managerOrder = new Users();
        Users managerStorekeeper = new Users();
        Store store = new Store();
        PaymentMethod paymentMethod = new PaymentMethod();

        if (createOrderDTO.getIdClient()  != null) {
            if(!iClientService.getClientByInternalReference(createOrderDTO.getIdClient()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
            client = iClientService.getClientByInternalReference(createOrderDTO.getIdClient()).get();

        }
        if (createOrderDTO.getIdManagerCoupon()  != null) {
            managerCoupon = iUserService.getByInternalReference(createOrderDTO.getIdManagerCoupon());

            if(managerCoupon.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdManagerOrder()  != null) {
            managerOrder = iUserService.getByInternalReference(createOrderDTO.getIdManagerOrder());

            if(managerOrder.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdStorekeeper()  != null) {
            managerStorekeeper = iUserService.getByInternalReference(createOrderDTO.getIdStorekeeper());

            if(managerStorekeeper.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdFund()  != null) {
            fund = iUserService.getByInternalReference(createOrderDTO.getIdFund());

            if(fund.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdStore()  != null) {
            if(!iStoreService.getByInternalReference(createOrderDTO.getIdStore()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store = iStoreService.getByInternalReference(createOrderDTO.getIdStore()).get();

        }
        if (createOrderDTO.getIdPaymentMethod()  != null) {
            if(!iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.payment_exists", null, LocaleContextHolder.getLocale())));
            paymentMethod = iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).get();

        }

        Long internalReference = jwtUtils.generateInternalReference();
        Order order = new Order();
        order.setInternalReference(internalReference);
        order.setCreatedAt(LocalDateTime.now());
        order.setClientReference(createOrderDTO.getClientReference());
        order.setIdClient(createOrderDTO.getIdClient());
        order.setIdFund(createOrderDTO.getIdFund());
        order.setIdManagerCoupon(createOrderDTO.getIdManagerCoupon());
        order.setIdManagerOrder(createOrderDTO.getIdManagerOrder());
        order.setChannel(createOrderDTO.getChannel());
        order.setDescription(createOrderDTO.getDescription());
        order.setDeliveryTime(createOrderDTO.getDeliveryTime());
        order.setIdPaymentMethod(createOrderDTO.getIdPaymentMethod());
        order.setNetAggregateAmount(createOrderDTO.getNetAggregateAmount());
        order.setTTCAggregateAmount(createOrderDTO.getTTCAggregateAmount());
        order.setTax(createOrderDTO.getTax());
        order.setIdStorekeeper(createOrderDTO.getIdStorekeeper());
        order.setIdStore(createOrderDTO.getIdStore());
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

        if(createOrderDTO.getIdManagerOrder() != null){
            emailToStore += managerOrder.getEmail();
        }
        String ClientReference = createOrderDTO.getIdClient()+" - "+client.getEmail()+" - "+client.getCompleteName()+" - "+client.getCompanyName();
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", internalReference);
        emailProps.put("internalReferenceClient", ClientReference);
        emailProps.put("internalReferenceStore", createOrderDTO.getIdStore());
        emailProps.put("delivryTime", createOrderDTO.getDeliveryTime());
        emailProps.put("canal", createOrderDTO.getChannel());
        emailProps.put("netAmount", createOrderDTO.getNetAggregateAmount());
        emailProps.put("ttcAmount", createOrderDTO.getTTCAggregateAmount());
        emailProps.put("payementMethode", (createOrderDTO.getIdPaymentMethod() == null)? "":createOrderDTO.getIdPaymentMethod()+ " - "+paymentMethod.getDesignation());

        if(emailToTresury != null){
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToTresury, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_ORDER+internalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_ORDER));
            log.info("Email  send successfull for user: " + emailToTresury);
        }

        if(emailToStore != null){
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToStore, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_ORDER+internalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_ORDER));
            log.info("Email  send successfull for user: " + emailToStore);
        }

        iOrderService.createOrder(order);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Modification des informations pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{InternalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO, @PathVariable Long InternalReference) {

        if (!iOrderService.getByInternalReference(InternalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_exists", null, LocaleContextHolder.getLocale())));
        }
        Order order = iOrderService.getByInternalReference(InternalReference).get();
        Client client = new Client();
        Users managerCoupon = new Users();
        Users fund = new Users();
        Users managerOrder = new Users();
        Users managerStorekeeper = new Users();
        Store store = new Store();
        PaymentMethod paymentMethod = new PaymentMethod();

        if (createOrderDTO.getIdClient()  != null) {
            if(!iClientService.getClientByInternalReference(createOrderDTO.getIdClient()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.client_exists", null, LocaleContextHolder.getLocale())));
            client = iClientService.getClientByInternalReference(createOrderDTO.getIdClient()).get();

        }
        if (createOrderDTO.getIdManagerCoupon()  != null) {
            managerCoupon = iUserService.getByInternalReference(createOrderDTO.getIdManagerCoupon());

            if(managerCoupon.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdManagerOrder()  != null) {
            managerOrder = iUserService.getByInternalReference(createOrderDTO.getIdManagerOrder());

            if(managerOrder.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdStorekeeper()  != null) {
            managerStorekeeper = iUserService.getByInternalReference(createOrderDTO.getIdStorekeeper());

            if(managerStorekeeper.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdFund()  != null) {
            fund = iUserService.getByInternalReference(createOrderDTO.getIdFund());

            if(fund.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }
        if (createOrderDTO.getIdStore()  != null) {
            if(!iStoreService.getByInternalReference(createOrderDTO.getIdStore()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store = iStoreService.getByInternalReference(createOrderDTO.getIdStore()).get();

        }
        if (createOrderDTO.getIdPaymentMethod()  != null) {
            if(!iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.payment_exists", null, LocaleContextHolder.getLocale())));
            paymentMethod = iPaymentMethodService.getByInternalReference(createOrderDTO.getIdPaymentMethod()).get();

        }
        order.setUpdateAt(LocalDateTime.now());
        order.setClientReference(createOrderDTO.getClientReference());
        if (createOrderDTO.getIdClient() != null)
            order.setIdClient(createOrderDTO.getIdClient());
        if (createOrderDTO.getIdFund() != null)
            order.setIdFund(createOrderDTO.getIdFund());
        if (createOrderDTO.getIdManagerCoupon() != null)
            order.setIdManagerCoupon(createOrderDTO.getIdManagerCoupon());
        if (createOrderDTO.getIdManagerOrder() != null)
            order.setIdManagerOrder(createOrderDTO.getIdManagerOrder());
        order.setChannel(createOrderDTO.getChannel());
        order.setDescription(createOrderDTO.getDescription());
        order.setDeliveryTime(createOrderDTO.getDeliveryTime());
        if (createOrderDTO.getIdPaymentMethod() != null)
            order.setIdPaymentMethod(createOrderDTO.getIdPaymentMethod());
        order.setNetAggregateAmount(createOrderDTO.getNetAggregateAmount());
        order.setTTCAggregateAmount(createOrderDTO.getTTCAggregateAmount());
        order.setTax(createOrderDTO.getTax());
        if (createOrderDTO.getIdStorekeeper() != null)
            order.setIdStorekeeper(createOrderDTO.getIdStorekeeper());
        if (createOrderDTO.getIdStore() != null)
            order.setIdStore(createOrderDTO.getIdStore());
        order.setPaymentReference(createOrderDTO.getPaymentReference());

        iOrderService.createOrder(order);

        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Effectuer un paiement pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/accept/{InternalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> acceptOrder(@RequestBody MultipartFile file, @PathVariable Long InternalReference, @RequestParam("idFund") Long idFund, @RequestParam("idPaymentMethod") Long idPaymentMethod, @RequestParam("paymentReference") String paymentReference, @RequestParam("docType") String docType) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(InternalReference).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        StatusOrder statusOrderCreated = iStatusOrderRepo.findByName(EStatusOrder.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.CREATED +  "  not found"));
        if (order.getStatus() != statusOrderCreated) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_created_before", null, LocaleContextHolder.getLocale())));
        }
        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.ACCEPTED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.ACCEPTED +  "  not found"));
        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.INVOICE).orElseThrow(()-> new ResourceNotFoundException("Statut :  "  +  ETypeDocument.INVOICE +  "  not found"));
        String fileName = iDocumentStorageService.storeFile(file, InternalReference, docType, typeDocument);
        String fileDownloadUri = api_base_url+"/api/v1.0/order/file/" + InternalReference + "/downloadFile?type=invoice&docType=" + docType;
        order.setLinkInvoice(fileDownloadUri);
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdPaymentMethod(idPaymentMethod);
        order.setIdFund(idFund);
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
        emailProps.put("internalReferenceOrder", InternalReference);
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.ACCEPTED);
        emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        List<Users> usersList = iUserService.getUsers();
        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.TREASURY) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+InternalReference+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }

            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_COUPON) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+InternalReference+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
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
    @PostMapping("/delivery/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> devliveryOrder(@PathVariable Long internalReference, @RequestParam("idManagerCoupon") Long idManagerCoupon) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(internalReference).get();
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
        order.setIdManagerCoupon(idManagerCoupon);
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
    @GetMapping("/file/{InternalReference:[0-9]+}/downloadFile")
    public ResponseEntity<Object> downloadFile(@PathVariable("InternalReference") Long InternalReference, @Schema(required = true, allowableValues = {"INVOICE", "DELIVERY"}, description = "Type de document") @RequestParam("type") String type, @RequestParam("docType") String docType,
                                               HttpServletRequest request) {

        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.valueOf(type.toUpperCase())).orElseThrow(()-> new ResourceNotFoundException("Type de document:  "  +  type +  "  not found"));

        String fileName = iDocumentStorageService.getDocumentName(InternalReference, docType, typeDocument.getId());
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
    @PostMapping("/pay/{InternalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> payOrder(@PathVariable Long InternalReference, @RequestParam("idManagerCoupon") Long idManagerCoupon) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(InternalReference).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        StatusOrder statusOrderAccept = iStatusOrderRepo.findByName(EStatusOrder.ACCEPTED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.ACCEPTED +  "  not found"));
        if (order.getStatus() != statusOrderAccept) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_accept_before", null, LocaleContextHolder.getLocale())));
        }
        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.PAID).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.PAID +  "  not found"));
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdManagerCoupon(idManagerCoupon);
        iOrderService.createOrder(order);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", InternalReference);
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.PAID);
        emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        List<Users> usersList = iUserService.getUsers();
        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.STORE_KEEPER) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+InternalReference+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }

            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_COUPON) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+InternalReference+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }
        }
        if(client.getEmail() != null){
            byte[] data = generateReceived(order, client);

            Map<String, Object> emailProps2 = new HashMap<>();
            emailProps2.put("Internalreference", InternalReference);
            emailProps2.put("completename", client.getCompleteName());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_NEW_RECEIVED+InternalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_RECEIVED, data));
            log.info("Email send successfull for user: " + client.getEmail());
        }

        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Annuler une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/cancel/{InternalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long InternalReference, @RequestParam("idManagerCoupon") Long idManagerCoupon, @RequestParam("reasonForCancellation")  @Schema(description = "Raison d’annulation") String reasonForCancellation) {

        Order order = iOrderService.getByInternalReference(InternalReference).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();

        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.ORDER_CANCEL).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.ORDER_CANCEL +  "  not found"));
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdManagerCoupon(idManagerCoupon);
        order.setReasonForCancellation(reasonForCancellation);
        iOrderService.createOrder(order);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", InternalReference);
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.ORDER_CANCEL);
        emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        Users managerCoupon = iUserService.getByInternalReference(idManagerCoupon);
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, managerCoupon.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_ORDER+InternalReference+" - "+EStatusOrder.ORDER_CANCEL, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_ORDER));
        log.info("Email  send successfull for user: " + managerCoupon.getEmail());

        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_ORDER+InternalReference+" - "+EStatusOrder.ORDER_CANCEL, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_ORDER));
        log.info("Email  send successfull for user: " + client.getEmail());

        return ResponseEntity.ok(order);
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

            StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.ORDER_CANCEL).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.ORDER_CANCEL +  "  not found"));
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
            emailProps.put("status", EStatusOrder.ORDER_CANCEL);
            emailProps.put("payementMethode", (order.getIdPaymentMethod() == null)? "":order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_MULTI_ORDER+cancelMultiOrderDTO.getOrders().get(i)+" - "+EStatusOrder.ACCEPTED, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_ORDER));
            log.info("Email  send successfull for user: " + client.getEmail());

        }

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("order", cancelMultiOrderDTO.getOrders());
        Users managerCoupon = iUserService.getByInternalReference(cancelMultiOrderDTO.getIdManagerCoupon());
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, managerCoupon.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CANCEL_MULTI_ORDER+" - "+EStatusOrder.ORDER_CANCEL, ApplicationConstant.TEMPLATE_EMAIL_CANCEL_MULTI_ORDER));
        log.info("Email  send successfull for user: " + managerCoupon.getEmail());

        return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK, " Les commandes ont été annulé avec succèss !"));
    }

    @Operation(summary = "valider le bon de livraison et terminer pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/valid/delivery/{InternalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> validDeliveryOrder(@RequestBody MultipartFile file, @PathVariable Long InternalReference, @RequestParam("idManagerCoupon") Long idManagerCoupon) throws JRException, IOException {

        Order order = iOrderService.getByInternalReference(InternalReference).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        StatusOrder statusOrderDelivery = iStatusOrderRepo.findByName(EStatusOrder.IN_PROCESS_OF_DELIVERY).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.IN_PROCESS_OF_DELIVERY +  "  not found"));
        if (order.getStatus() != statusOrderDelivery) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_delivery_before", null, LocaleContextHolder.getLocale())));
        }
        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.CLOSED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.CLOSED +  "  not found"));
        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.DELIVERY).orElseThrow(()-> new ResourceNotFoundException("Statut :  "  +  ETypeDocument.DELIVERY +  "  not found"));
        String fileName = iDocumentStorageService.storeFile(file, InternalReference, "pdf", typeDocument);
        String fileDownloadUri = api_base_url+"/api/v1.0/order/file/" + InternalReference + "/downloadFile?type=delivery&docType=pdf";
        order.setLinkDelivery(fileDownloadUri);
        order.setStatus(statusOrder);
        order.setUpdateAt(LocalDateTime.now());
        order.setIdManagerCoupon(idManagerCoupon);
        iOrderService.createOrder(order);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", InternalReference);
        emailProps.put("internalReferenceClient", order.getClientReference());
        emailProps.put("internalReferenceStore", order.getIdStore());
        emailProps.put("delivryTime", order.getDeliveryTime());
        emailProps.put("canal", order.getChannel());
        emailProps.put("netAmount", order.getNetAggregateAmount());
        emailProps.put("ttcAmount", order.getTTCAggregateAmount());
        emailProps.put("status", EStatusOrder.CLOSED);
        emailProps.put("payementMethode", order.getIdPaymentMethod()+ " - "+iPaymentMethodService.getByInternalReference(order.getIdPaymentMethod()).get().getDesignation());

        List<Users> usersList = iUserService.getUsers();
        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_COUPON) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_ORDER+InternalReference+" - "+EStatusOrder.CLOSED, ApplicationConstant.TEMPLATE_EMAIL_MODIFY_ORDER));
                log.info("Email  send successfull for user: " + user.getEmail());
            }
        }
        if(client.getEmail() != null){
            byte[] data = generateFacture(order, client);

            Map<String, Object> emailProps2 = new HashMap<>();
            emailProps2.put("Internalreference", InternalReference);
            emailProps2.put("completename", client.getCompleteName());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_NEW_FACTURE+InternalReference, ApplicationConstant.TEMPLATE_EMAIL_NEW_FACTURE, data));
            log.info("Email send successfull for user: " + client.getEmail());
        }


        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Recupérer la liste des commandes par client sous forme de fichier excel et envoyé par Mail", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/export/excel/client/{idClient:[0-9]+}")
    public ResponseEntity<?> exportOrdersByIdClient(@PathVariable Long idClient) {
        Client client = iClientService.getClientByInternalReference(idClient).get();
        ByteArrayInputStream data = iOrderService.exportOrdersByIdClient(idClient);

        Map<String, Object> emailProps2 = new HashMap<>();
        emailProps2.put("Internalreference", idClient);
        emailProps2.put("CompleteName", client.getCompleteName());
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, client.getEmail(), mailReplyTo, emailProps2, ApplicationConstant.SUBJECT_EMAIL_EXPORT_ORDERS_EXCEL, ApplicationConstant.TEMPLATE_EMAIL_EXPORT_ORDER_EXCEL, data.readAllBytes(),"export-commandes-" + idClient + ".xlsx", "application/vnd.ms-excel"));
        log.info("Email send successfull for user: " + client.getEmail());


        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=export-commandes-" + idClient + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));

        return ResponseEntity.ok().headers(headers).body(data.readAllBytes());
    }

    @Operation(summary = "Recupérer la liste des commandes par client", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/client/{idClient:[0-9]+}")
    public ResponseEntity<Page<ResponseOrderDTO>> getOrdersByIdClient(@PathVariable Long idClient,
                                                           @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                           @RequestParam(required = false, defaultValue = "idClient") String sort,
                                                           @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<ResponseOrderDTO> orders = iOrderService.getOrdersByIdClient(idClient,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
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


    @Operation(summary = "Recupérer la liste des commandes par gestionnaire de coupon", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/managercoupon/{idManagerCoupon:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdManagerCoupon(@PathVariable Long idManagerCoupon,
                                                                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                  @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                  @RequestParam(required = false, defaultValue = "idManagerCoupon") String sort,
                                                                  @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdManagerCoupon(idManagerCoupon,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Recupérer la liste des commandes par gestionnaire de commandes", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/managerstore/{idManagerOrder:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdManagerStore(@PathVariable Long idManagerOrder,
                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                 @RequestParam(required = false, defaultValue = "idManagerOrder") String sort,
                                                                 @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdManagerOrder(idManagerOrder,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }


    @Operation(summary = "Recupérer la liste des commandes par magasinier", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/storekeeper/{idStorekeeper:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdStorekeeper(@PathVariable Long idStorekeeper,
                                                                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                  @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                  @RequestParam(required = false, defaultValue = "idStorekeeper") String sort,
                                                                  @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdStorekeeper(idStorekeeper,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(orders);
    }

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

    @Operation(summary = "Recupérer Une commande par son Identifiant interne", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Order> getOrderByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iOrderService.getByInternalReference(internalReference).get());
    }


    @Operation(summary = "La proforma numérique ou la préfacture numérique générée pour une commande par son Identifiant interne", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/invoice/{internalReference:[0-9]+}")
    public ResponseEntity<?> generateInvoiceByInternalReference(@PathVariable Long internalReference) throws JRException, IOException {
        Order order = iOrderService.getByInternalReference(internalReference).get();
        Client client = iClientService.getClientByInternalReference(order.getIdClient()).get();
        byte[] data = generateInvoice(order, client);
        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());
        if (products.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.noproduct", null, LocaleContextHolder.getLocale())));
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.INSTITUTION)){
            testTypeDocument = true;
        }
        Map<String, Object> emailProps2 = new HashMap<>();
        emailProps2.put("Internalreference", internalReference);
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
    @PostMapping("/document/{internalReference:[0-9]+}")
    public ResponseEntity<?> generateInvoiceOrDelivery(@PathVariable Long internalReference, @Schema(required = true, allowableValues = {"INVOICE", "DELIVERY"}, description = "Type de document") @RequestParam("type") String type) throws JRException, IOException {
        Order order = iOrderService.getByInternalReference(internalReference).get();
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
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws IOException {
        Page<ResponseOrderDTO> list = iOrderService.getAllOrders(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);

        return ResponseEntity.ok(list);
    }

    private byte[] generateInvoice(Order order, Client client) throws JRException, IOException {
        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());

        ProductDTO productDTO = new ProductDTO();
        List<ProductDTO> productDTOList = new ArrayList<>();

        for(Product product : products){
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()+"");
            productDTO.setValeur(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+product.getInternalReference());
            productDTOList.add(productDTO);
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.INSTITUTION)){
            testTypeDocument = true;
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("typeDocument", testTypeDocument? "PREFACTURE":"PROFORMA");
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", order.getTax()+"");
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
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()+"");
            productDTO.setValeur(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+product.getInternalReference());
            productDTOList.add(productDTO);
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.INSTITUTION)){
            testTypeDocument = true;
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("type", testTypeDocument? "PREFACTURE":"PROFORMA");
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", order.getTax()+"");
        parameters.put("dateOrder", dateFor.format(Date.from((order.getUpdateAt() == null) ? order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  order.getUpdateAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("TTCAggregateAmount", order.getTTCAggregateAmount()+"");
        parameters.put("completeName", client.getCompleteName());
        parameters.put("companyName", client.getCompanyName());
        parameters.put("address", (client.getAddress() != null) ? client.getAddress(): " - ");
        parameters.put("phone", (client.getPhone() != null)? client.getPhone()+"" : " - ");
        parameters.put("email", (client.getEmail() != null)? client.getEmail(): " - ");
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
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()+"");
            productDTO.setValeur(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+product.getInternalReference());
            productDTOList.add(productDTO);
        }
        boolean testTypeDocument = false;

        if(client.getTypeClient().getName().equals(ETypeClient.INSTITUTION)){
            testTypeDocument = true;
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("type", testTypeDocument? "PREFACTURE":"PROFORMA");
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", order.getTax()+"");
        parameters.put("dateOrder", dateFor.format(Date.from((order.getUpdateAt() == null) ? order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  order.getUpdateAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("TTCAggregateAmount", order.getTTCAggregateAmount()+"");
        parameters.put("completeName", client.getCompleteName());
        parameters.put("companyName", client.getCompanyName());
        parameters.put("address", (client.getAddress() != null) ? client.getAddress(): " - ");
        parameters.put("phone", (client.getPhone() != null)? client.getPhone()+"" : " - ");
        parameters.put("email", (client.getEmail() != null)? client.getEmail(): " - ");
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
            productDTO = new ProductDTO();
            productDTO.setQuantityNotebook(product.getQuantityNotebook());
            productDTO.setPu(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()+"");
            productDTO.setValeur(iTypeVoucherService.getByInternalReference(product.getIdTypeVoucher()).get().getAmount()*product.getQuantityNotebook()+"");
            productDTO.setProduct("Carnet N-"+product.getInternalReference());
            productDTOList.add(productDTO);
        }
        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("NetAggregateAmount", order.getNetAggregateAmount()+"");
        parameters.put("tax", order.getTax()+"");
        parameters.put("dateOrder", dateFor.format(Date.from((order.getUpdateAt() == null) ? order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  order.getUpdateAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("TTCAggregateAmount", order.getTTCAggregateAmount()+"");
        parameters.put("completeName", client.getCompleteName());
        parameters.put("companyName", client.getCompanyName());
        parameters.put("address", (client.getAddress() != null) ? client.getAddress(): " - ");
        parameters.put("phone", (client.getPhone() != null)? client.getPhone()+"" : " - ");
        parameters.put("email", (client.getEmail() != null)? client.getEmail(): " - ");
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
