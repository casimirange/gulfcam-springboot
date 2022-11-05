package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.store.dto.CreateCartonDTO;
import com.gulfcam.fuelcoupon.store.dto.CreateNotebookDTO;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.Notebook;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.repository.INotebookRepo;
import com.gulfcam.fuelcoupon.store.service.ICartonService;
import com.gulfcam.fuelcoupon.store.service.INotebookService;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@Tag(name = "Carnet")
@RequestMapping("/api/v1.0/notebook")
@Slf4j
public class NotebookRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;


    @Autowired
    INotebookService iNotebookService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    INotebookRepo iNotebookRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un Carnet", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Notebook.class)))),
            @ApiResponse(responseCode = "404", description = "Carnet not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addNotebook(@Valid @RequestBody CreateNotebookDTO createNotebookDTO) {

        if (iNotebookService.existsNotebookBySerialNumber(createNotebookDTO.getSerialNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.serial_exists", null, LocaleContextHolder.getLocale())));
        }

        Notebook notebook = new Notebook();
        notebook.setInternalReference(jwtUtils.generateInternalReference());
        notebook.setCreatedAt(LocalDateTime.now());
        notebook.setSerialNumber(createNotebookDTO.getSerialNumber());
        notebook.setIdCarton(createNotebookDTO.getIdCarton());
        notebook.setIdStoreKeeper(createNotebookDTO.getIdStoreKeeper());

        iNotebookService.createNotebook(notebook);
        return ResponseEntity.ok(notebook);
    }

    @Operation(summary = "Modification des informations pour un Carnet", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Notebook.class)))),
            @ApiResponse(responseCode = "404", description = "Carnet not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateNotebook(@Valid @RequestBody CreateNotebookDTO createNotebookDTO, @PathVariable Long internalReference) {

        Notebook notebook = iNotebookService.getByInternalReference(internalReference).get();
        if (notebook.getId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.notebook_exists", null, LocaleContextHolder.getLocale())));
        }
        notebook.setUpdateAt(LocalDateTime.now());
        notebook.setSerialNumber(createNotebookDTO.getSerialNumber());
        notebook.setIdCarton(createNotebookDTO.getIdCarton());
        notebook.setIdStoreKeeper(createNotebookDTO.getIdStoreKeeper());

        iNotebookService.createNotebook(notebook);

        return ResponseEntity.ok(notebook);
    }

    @Operation(summary = "Recupérer la liste des Carnets par carton", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Notebook.class)))),
            @ApiResponse(responseCode = "404", description = "Carnet not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/carton/{idCarton:[0-9]+}")
    public ResponseEntity<Page<Notebook>> getNotebooksByIdCarton(@PathVariable Long idCarton,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idCarton") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Notebook> notebooks = iNotebookService.getNotebooksByIdCarton(idCarton,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(notebooks);
    }

    @Operation(summary = "Recupérer la liste des Carnets par Entrepôt", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Notebook.class)))),
            @ApiResponse(responseCode = "404", description = "Carnet not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/storekeeper/{idStoreKeeper:[0-9]+}")
    public ResponseEntity<Page<Notebook>> getNotebooksByIdStoreKeeper(@PathVariable Long idStoreKeeper,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idStoreKeeper") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Notebook> notebooks = iNotebookService.getNotebooksByIdStoreKeeper(idStoreKeeper,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(notebooks);
    }

    @Operation(summary = "Recupérer Un Carnets par son Numéro de série", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Notebook.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<Notebook> getNotebookBySerialNumber(@PathVariable String serialNumber) {
        return ResponseEntity.ok(iNotebookService.getNotebookBySerialNumber(serialNumber).get());
    }

    @Operation(summary = "Recupérer Un Carnet par sa reference interne", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Notebook.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Notebook> getNotebookByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iNotebookService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer un Carnet", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "200", description = "Carnet deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteNotebook(@PathVariable Long internalReference) {
        Notebook notebook = iNotebookService.getByInternalReference(internalReference).get();
        iNotebookService.deleteNotebook(notebook);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Carnets", tags = "Carnet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Carnet not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllNotebooks(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Notebook> list = iNotebookService.getAllNotebooks(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
