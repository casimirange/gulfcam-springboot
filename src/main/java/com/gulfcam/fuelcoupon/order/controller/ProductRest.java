package com.gulfcam.fuelcoupon.order.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.dto.CreateItemDTO;
import com.gulfcam.fuelcoupon.order.dto.CreateProductDTO;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.entity.Product;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.repository.IItemRepo;
import com.gulfcam.fuelcoupon.order.repository.IProductRepo;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.IOrderService;
import com.gulfcam.fuelcoupon.order.service.IProductService;
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
@Tag(name = "Produits")
@RequestMapping("/api/v1.0/product")
@Slf4j
public class ProductRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    IProductService iProductService;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IOrderService iOrderService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IProductRepo iProductRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un Produit", tags = "Produits", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
            @ApiResponse(responseCode = "404", description = "product not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addProduct(@Valid @RequestBody CreateProductDTO createProductDTO) {

        Order order= new Order();
        TypeVoucher typeVoucher = new TypeVoucher();
        if (createProductDTO.getIdTypeVoucher()  != null) {
            if(!iTypeVoucherService.getByInternalReference(createProductDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typevoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createProductDTO.getIdTypeVoucher()).get();

        }

        if (createProductDTO.getIdOrder()  != null) {
            if(!iOrderService.getByInternalReference(createProductDTO.getIdOrder()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.order_exists", null, LocaleContextHolder.getLocale())));
            order = iOrderService.getByInternalReference(createProductDTO.getIdOrder()).get();

        }
        Product product = new Product();
        product.setInternalReference(jwtUtils.generateInternalReference());
        product.setCreatedAt(LocalDateTime.now());
        product.setIdTypeVoucher(createProductDTO.getIdTypeVoucher());
        product.setIdOrder(createProductDTO.getIdOrder());
        product.setQuantityNotebook(createProductDTO.getQuantityNotebook());

        Status status = iStatusRepo.findByName(EStatus.STORE_ENABLE).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.STORE_ENABLE +  "  not found"));
        product.setStatus(status);

        iProductService.createProduct(product);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Modification des informations pour un Produit", tags = "Produits", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
            @ApiResponse(responseCode = "404", description = "product not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody CreateProductDTO createProductDTO, @PathVariable Long internalReference) {

        if (!iProductService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.product_exists", null, LocaleContextHolder.getLocale())));
        }
        Product product = iProductService.getByInternalReference(internalReference).get();
        Order order= new Order();
        TypeVoucher typeVoucher = new TypeVoucher();
        if (createProductDTO.getIdTypeVoucher()  != null) {
            if(!iTypeVoucherService.getByInternalReference(createProductDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typevoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createProductDTO.getIdTypeVoucher()).get();

        }

        if (createProductDTO.getIdOrder()  != null) {
            if(!iOrderService.getByInternalReference(createProductDTO.getIdOrder()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.order_exists", null, LocaleContextHolder.getLocale())));
            order = iOrderService.getByInternalReference(createProductDTO.getIdOrder()).get();

        }
        product.setUpdateAt(LocalDateTime.now());
        if (createProductDTO.getIdTypeVoucher() != null)
            product.setIdTypeVoucher(createProductDTO.getIdTypeVoucher());
        if (createProductDTO.getIdOrder() != null)
            product.setIdOrder(createProductDTO.getIdOrder());
        product.setQuantityNotebook(createProductDTO.getQuantityNotebook());

        iProductService.createProduct(product);

        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Recupérer la liste des Produits par type de bon", tags = "Produits", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
            @ApiResponse(responseCode = "404", description = "product not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/typevoucher/{idTypeVoucher:[0-9]+}")
    public ResponseEntity<Page<Product>> getProductsByIdTypeVoucher(@PathVariable Long idTypeVoucher,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idTypeVoucher") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Product> products = iProductService.getProductsByIdTypeVoucher(idTypeVoucher,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Recupérer la liste des Produits par commande", tags = "Produits", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
            @ApiResponse(responseCode = "404", description = "product not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/order/{idOrder:[0-9]+}")
    public ResponseEntity<Page<Product>> getProductsByIdOrder(@PathVariable Long idOrder,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idOrder") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Product> products = iProductService.getProductsByIdOrder(idOrder,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Recupérer Un Produit par sa reference interne", tags = "Produits", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Product> getProductByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iProductService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer un Produit", tags = "Produits", responses = {
            @ApiResponse(responseCode = "200", description = "product deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long internalReference) {
        Product product = iProductService.getByInternalReference(internalReference).get();
        iProductService.deleteProduct(product);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Produits", tags = "Produits", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "product not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getItems(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Product> list = iProductService.getAllProducts(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
