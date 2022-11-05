package com.gulfcam.fuelcoupon.order.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.entity.ETypeClient;
import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.dto.CreateItemDTO;
import com.gulfcam.fuelcoupon.order.dto.CreateOrderDTO;
import com.gulfcam.fuelcoupon.order.entity.EStatusOrder;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.entity.StatusOrder;
import com.gulfcam.fuelcoupon.order.repository.IItemRepo;
import com.gulfcam.fuelcoupon.order.repository.IOrderRepo;
import com.gulfcam.fuelcoupon.order.repository.IStatusOrderRepo;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.IOrderService;
import com.gulfcam.fuelcoupon.user.dto.EmailDto;
import com.gulfcam.fuelcoupon.user.entity.ETypeAccount;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
import com.gulfcam.fuelcoupon.user.service.IUserService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    IStatusOrderRepo iStatusOrderRepo;

    @Autowired
    IOrderRepo iOrderRepo;

    @Autowired
    IUserService iUserService;

    @Autowired
    IClientService iClientService;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour une commande", tags = "Order", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {

        Long internalReference = jwtUtils.generateInternalReference();
        Order order = new Order();
        order.setInternalReference(internalReference);
        order.setCreatedAt(LocalDateTime.now());
        order.setClientReference(createOrderDTO.getClientReference());
        order.setIdClient(createOrderDTO.getIdClient());
        order.setIdFund(createOrderDTO.getIdFund());
        order.setIdManagerCoupon(createOrderDTO.getIdManagerCoupon());
        order.setIdManagerCoupon(createOrderDTO.getIdManagerCoupon());
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
        order.setReasonForCancellation(createOrderDTO.getReasonForCancellation());

        StatusOrder statusOrder = iStatusOrderRepo.findByName(EStatusOrder.ORDER_CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatusOrder.ORDER_CREATED +  "  not found"));
        order.setStatus(statusOrder);

        List<Users> usersList = iUserService.getUsers();

        String emailToTresury = "";
        String emailToStore = "";

        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.TREASURY) {
                emailToTresury = user.getEmail();
            }
        }

        if(createOrderDTO.getIdManagerStore() != null){
            Users userManagerStore = iUserService.getByInternalReference(createOrderDTO.getIdManagerStore());
            emailToStore += userManagerStore.getEmail();
        }

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("internalReferenceOrder", internalReference);
        emailProps.put("internalReferenceClient", createOrderDTO.getIdClient());
        emailProps.put("internalReferenceStore", createOrderDTO.getIdStore());
        emailProps.put("delivryTime", createOrderDTO.getDeliveryTime());
        emailProps.put("canal", createOrderDTO.getChannel());
        emailProps.put("netAmount", createOrderDTO.getNetAggregateAmount());
        emailProps.put("ttcAmount", createOrderDTO.getTTCAggregateAmount());
        emailProps.put("payementMethode", createOrderDTO.getIdPaymentMethod());

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

        Order order = iOrderService.getByInternalReference(InternalReference).get();
        if (order.getId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.order_exists", null, LocaleContextHolder.getLocale())));
        }
        order.setUpdateAt(LocalDateTime.now());
        order.setClientReference(createOrderDTO.getClientReference());
        order.setIdClient(createOrderDTO.getIdClient());
        order.setIdFund(createOrderDTO.getIdFund());
        order.setIdManagerCoupon(createOrderDTO.getIdManagerCoupon());
        order.setIdManagerCoupon(createOrderDTO.getIdManagerCoupon());
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
        order.setReasonForCancellation(createOrderDTO.getReasonForCancellation());

        iOrderService.createOrder(order);

        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Recupérer la liste des commandes par client", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/client/{idClient:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdClient(@PathVariable Long idClient,
                                                           @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                           @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                           @RequestParam(required = false, defaultValue = "idClient") String sort,
                                                           @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdClient(idClient,
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

    @Operation(summary = "Recupérer la liste des commandes par gestionnaire de magasin", tags = "Order", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/managerstore/{idManagerStore:[0-9]+}")
    public ResponseEntity<Page<Order>> getOrdersByIdManagerStore(@PathVariable Long idManagerStore,
                                                                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                 @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                 @RequestParam(required = false, defaultValue = "idManagerStore") String sort,
                                                                 @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Order> orders = iOrderService.getOrdersByIdManagerStore(idManagerStore,
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
    public ResponseEntity<?> getItems(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Order> list = iOrderService.getAllOrders(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
