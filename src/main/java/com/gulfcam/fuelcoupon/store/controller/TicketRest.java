package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.store.dto.CreateTicketDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import com.gulfcam.fuelcoupon.store.entity.Ticket;
import com.gulfcam.fuelcoupon.store.repository.ITicketRepo;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
import com.gulfcam.fuelcoupon.store.service.IRequestOppositionService;
import com.gulfcam.fuelcoupon.store.service.ITicketService;
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
@Tag(name = "Ticket")
@RequestMapping("/api/v1.0/ticket")
@Slf4j
public class TicketRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    ITicketService iTicketService;

    @Autowired
    IRequestOppositionService iRequestOppositionService;

    @Autowired
    ICouponService iCouponService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    ITicketRepo iTicketRepo;

    @Autowired
    ApplicationContext appContext;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un Ticket", tags = "Ticket", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Ticket.class)))),
            @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addTicket(@Valid @RequestBody CreateTicketDTO createTicketDTO) {

        Coupon coupon = new Coupon();
        RequestOpposition requestOpposition = new RequestOpposition();
        if (createTicketDTO.getIdCoupon() != null) {
            if(!iCouponService.getByInternalReference(createTicketDTO.getIdCoupon()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
            coupon = iCouponService.getByInternalReference(createTicketDTO.getIdCoupon()).get();
        }
        if (createTicketDTO.getIdRequestOpposition() != null) {
            if(!iRequestOppositionService.getByInternalReference(createTicketDTO.getIdRequestOpposition()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.requestopposition_exists", null, LocaleContextHolder.getLocale())));
            requestOpposition = iRequestOppositionService.getByInternalReference(createTicketDTO.getIdRequestOpposition()).get();
        }
        Ticket ticket = new Ticket();
        ticket.setInternalReference(jwtUtils.generateInternalReference());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setIdCoupon(createTicketDTO.getIdCoupon());
        ticket.setIdRequestOpposition(createTicketDTO.getIdRequestOpposition());

        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        ticket.setStatus(status);

        iTicketService.createTicket(ticket);
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Modification des informations pour un Ticket", tags = "Ticket", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Ticket.class)))),
            @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateTicket(@Valid @RequestBody CreateTicketDTO createTicketDTO, @PathVariable Long internalReference) {

        if (!iTicketService.getByInternalReference(internalReference).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.ticket_exists", null, LocaleContextHolder.getLocale())));
        }
        Ticket ticket = iTicketService.getByInternalReference(internalReference).get();
        Coupon coupon = new Coupon();
        RequestOpposition requestOpposition = new RequestOpposition();
        if (createTicketDTO.getIdCoupon() != null) {
            if(!iCouponService.getByInternalReference(createTicketDTO.getIdCoupon()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
            coupon = iCouponService.getByInternalReference(createTicketDTO.getIdCoupon()).get();
        }
        if (createTicketDTO.getIdRequestOpposition() != null) {
            if(!iRequestOppositionService.getByInternalReference(createTicketDTO.getIdRequestOpposition()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.requestopposition_exists", null, LocaleContextHolder.getLocale())));
            requestOpposition = iRequestOppositionService.getByInternalReference(createTicketDTO.getIdRequestOpposition()).get();
        }
        ticket.setUpdateAt(LocalDateTime.now());
        if (createTicketDTO.getIdCoupon() != null)
            ticket.setIdCoupon(createTicketDTO.getIdCoupon());
        if (createTicketDTO.getIdRequestOpposition() != null)
            ticket.setIdRequestOpposition(createTicketDTO.getIdRequestOpposition());

        iTicketService.createTicket(ticket);

        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Recupérer la liste des Tickets par coupon", tags = "Ticket", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Ticket.class)))),
            @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/coupon/{idCoupon:[0-9]+}")
    public ResponseEntity<Page<Ticket>> getTicketsByIdCoupon(@PathVariable Long idCoupon,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "idCoupon") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Ticket> tickets = iTicketService.getTicketsByIdCoupon(idCoupon,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Recupérer la liste des Tickets par requete d'opposition", tags = "Ticket", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Ticket.class)))),
            @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/requestopposition/{IdRequestOpposition:[0-9]+}")
    public ResponseEntity<Page<Ticket>> getTicketsByIdRequestOpposition(@PathVariable Long IdRequestOpposition,
                                                              @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                              @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                              @RequestParam(required = false, defaultValue = "IdRequestOpposition") String sort,
                                                              @RequestParam(required = false, defaultValue = "desc") String order) {

        Page<Ticket> tickets = iTicketService.getTicketsByIdRequestOpposition(IdRequestOpposition,
                Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Recupérer Un Ticket par sa reference interne", tags = "Ticket", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Ticket.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Ticket> getTicketByInternalReference(@PathVariable Long internalReference) {
        return ResponseEntity.ok(iTicketService.getByInternalReference(internalReference).get());
    }

    @Operation(summary = "Supprimer un Ticket", tags = "Ticket", responses = {
            @ApiResponse(responseCode = "200", description = "Ticket deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteTicket(@PathVariable Long internalReference) {
        Ticket ticket = iTicketService.getByInternalReference(internalReference).get();
        iTicketService.deleteTicket(ticket);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Tickets", tags = "Carton", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllTickets(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Ticket> list = iTicketService.getAllTickets(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
