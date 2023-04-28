package com.gulfcam.fuelcoupon.order.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.dto.CreateItemDTO;
import com.gulfcam.fuelcoupon.order.dto.ResponseItemDTO;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.repository.IItemRepo;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
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
@Tag(name = "Pièce")
@RequestMapping("/api/v1.0/item")
@Slf4j
public class ItemRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    IItemService iItemService;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IItemRepo iItemRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour une pièce", tags = "Pièce", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addItem(@Valid @RequestBody CreateItemDTO createItemDTO) {

        TypeVoucher typeVoucher = new TypeVoucher();
        Storehouse storehouse = new Storehouse();
        if (createItemDTO.getIdTypeVoucher()  != null) {
            if(!iTypeVoucherService.getByInternalReference(createItemDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typevoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createItemDTO.getIdTypeVoucher()).get();

        }

        if (createItemDTO.getIdStoreHouse()  != null) {
            if(!iStorehouseService.getByInternalReference(createItemDTO.getIdStoreHouse()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse = iStorehouseService.getByInternalReference(createItemDTO.getIdStoreHouse()).get();

        }

        Item item = new Item();
        item.setInternalReference(jwtUtils.generateInternalReference());
        item.setCreatedAt(LocalDateTime.now());
        item.setIdTypeVoucher(createItemDTO.getIdTypeVoucher());
        item.setIdStoreHouse(createItemDTO.getIdStoreHouse() );
        item.setQuantityCarton(createItemDTO.getQuantityCarton());
        item.setQuantityNotebook(createItemDTO.getQuantityNotebook());

        iItemService.createItem(item);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Modification des informations pour une pièce", tags = "Pièce", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateItem(@Valid @RequestBody CreateItemDTO createItemDTO, @PathVariable Long internalReference) {

        if (!iItemService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.item_exists", null, LocaleContextHolder.getLocale())));
        }
        Item item = iItemService.getByInternalReference(internalReference).get();
        TypeVoucher typeVoucher = new TypeVoucher();
        Storehouse storehouse = new Storehouse();
        if (createItemDTO.getIdTypeVoucher()  != null) {
            if(!iTypeVoucherService.getByInternalReference(createItemDTO.getIdTypeVoucher()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.typevoucher_exists", null, LocaleContextHolder.getLocale())));
            typeVoucher = iTypeVoucherService.getByInternalReference(createItemDTO.getIdTypeVoucher()).get();

        }

        if (createItemDTO.getIdStoreHouse()  != null) {
            if(!iStorehouseService.getByInternalReference(createItemDTO.getIdStoreHouse()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.storehouse_exists", null, LocaleContextHolder.getLocale())));
            storehouse = iStorehouseService.getByInternalReference(createItemDTO.getIdStoreHouse()).get();

        }
        item.setUpdateAt(LocalDateTime.now());
        if (createItemDTO.getIdTypeVoucher() != null)
            item.setIdTypeVoucher(createItemDTO.getIdTypeVoucher());
        if (createItemDTO.getIdStoreHouse()  != null)
            item.setIdStoreHouse(createItemDTO.getIdStoreHouse());
        item.setQuantityCarton(createItemDTO.getQuantityCarton());
        item.setQuantityNotebook(createItemDTO.getQuantityNotebook());
        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        item.setStatus(status);

        iItemService.createItem(item);

        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Recupérer la liste des pièce par type de bon", tags = "Pièce", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/typevoucher/{idTypeVoucher:[0-9]+}")
    public ResponseEntity<Page<Item>> getItemsByIdTypeVoucher(@PathVariable Long idTypeVoucher,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idTypeVoucher") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Item> items = iItemService.getItemsByIdTypeVoucher(idTypeVoucher,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Recupérer la liste des pièce par Entrepôt", tags = "Pièce", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/storehouse/{idStoreHouse:[0-9]+}")
    public ResponseEntity<Page<Item>> getItemsByIdStoreHouse(@PathVariable Long idStoreHouse,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idTypeVoucher") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Item> items = iItemService.getItemsByIdStoreHouse(idStoreHouse,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Recupérer Une pièce par sa reference interne", tags = "Pièce", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Item> getItemByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iItemService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer une pièce", tags = "Pièce", responses = {
            @ApiResponse(responseCode = "200", description = "Item deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long internalReference) {
        Item item = iItemService.getByInternalReference(internalReference).get();
        iItemService.deleteItem(item);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Pièces", tags = "Pièce", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getItems(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<ResponseItemDTO> list = iItemService.getAllItems(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
