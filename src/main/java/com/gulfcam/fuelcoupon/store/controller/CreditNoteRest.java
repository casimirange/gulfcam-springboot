package com.gulfcam.fuelcoupon.store.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.dto.CreateCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.dto.CreateRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponMailDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.repository.IRequestOppositionRepo;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
import com.gulfcam.fuelcoupon.store.service.ICreditNoteService;
import com.gulfcam.fuelcoupon.store.service.IRequestOppositionService;
import com.gulfcam.fuelcoupon.store.service.IStationService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Note de crédit")
@RequestMapping("/api/v1.0/creditnote")
@Slf4j
public class CreditNoteRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    ICreditNoteService iCreditNoteService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStationService iStationService;

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

    @Operation(summary = "Valider les coupons utilisés", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addCreditNote(@Valid @RequestBody CreateCreditNoteDTO createCreditNoteDTO) {

        Coupon coupon;
        ResponseCouponMailDTO responseCouponMailDTO;
        CreditNote creditNote = new CreditNote();
        creditNote.setInternalReference(jwtUtils.generateInternalReference());
        creditNote.setIdStation(createCreditNoteDTO.getIdStation());
        creditNote.setCreatedAt(LocalDateTime.now());
        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        creditNote.setStatus(status);

        iCreditNoteService.createCreditNote(creditNote);
        List<ResponseCouponMailDTO> couponList = new ArrayList<>();
        for (int i = 0; i< createCreditNoteDTO.getSerialCoupons().size(); i++){
            coupon = iCouponService.getCouponBySerialNumber(createCreditNoteDTO.getSerialCoupons().get(i)).get();

            responseCouponMailDTO= new ResponseCouponMailDTO();
            responseCouponMailDTO.setIdTypeVoucher(iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponMailDTO.setInternalReference(coupon.getInternalReference());
            responseCouponMailDTO.setStatus(coupon.getStatus());
            responseCouponMailDTO.setSerialNumber(coupon.getSerialNumber());
            coupon.setIdCreditNote(creditNote.getInternalReference());
            coupon.setUpdateAt(LocalDateTime.now());
            coupon.setIdStation(createCreditNoteDTO.getIdStation());
            iCouponService.createCoupon(coupon);
            couponList.add(responseCouponMailDTO);

        }

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("couponList", couponList);

        List<Users> usersList = iUserService.getUsers();

        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.TREASURY) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_CREDIT_NOTE));
            }
            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_STATION) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_CREDIT_NOTE));
            }
            if (user.getTypeAccount().getName() == ETypeAccount.CUSTOMER_SERVICE) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_CREDIT_NOTE));
            }
        }

        return ResponseEntity.ok(creditNote);
    }

    @Operation(summary = "Recupérer Une Note de crédit par sa reference interne", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = CreditNote.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<ResponseCreditNoteDTO> getByInternalReference(@PathVariable Long internalReference) {

        CreditNote creditNote = iCreditNoteService.getByInternalReference(internalReference).get();
        ResponseCreditNoteDTO responseCreditNoteDTO = new ResponseCreditNoteDTO();

        responseCreditNoteDTO.setCoupon(iCouponService.getCouponsByIdCreditNote(internalReference));
        responseCreditNoteDTO.setStation(iStationService.getByInternalReference(creditNote.getIdStation()).get());
        responseCreditNoteDTO.setInternalReference(creditNote.getInternalReference());
        responseCreditNoteDTO.setStatus(creditNote.getStatus());
        responseCreditNoteDTO.setId(creditNote.getId());
        responseCreditNoteDTO.setUpdateAt(creditNote.getUpdateAt());
        responseCreditNoteDTO.setCreatedAt(creditNote.getCreatedAt());

        creditNote.setCreatedAt(LocalDateTime.now());
        Status status = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.ACTIVATED +  "  not found"));
        creditNote.setStatus(status);
        iCreditNoteService.createCreditNote(creditNote);


        return ResponseEntity.ok(responseCreditNoteDTO);
    }

    @Operation(summary = "Valider Une Note de crédit par sa reference interne", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = CreditNote.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/valid/{internalReference:[0-9]+}")
    public ResponseEntity<ResponseCreditNoteDTO> validCreditNote(@PathVariable Long internalReference) {

        CreditNote creditNote = iCreditNoteService.getByInternalReference(internalReference).get();
        ResponseCreditNoteDTO responseCreditNoteDTO = new ResponseCreditNoteDTO();
        float amoutToDebit = 0;
        creditNote.setUpdateAt(LocalDateTime.now());
        Status status = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.ACTIVATED +  "  not found"));
        creditNote.setStatus(status);
        iCreditNoteService.createCreditNote(creditNote);

        List<Coupon> coupons = iCouponService.getCouponsByIdCreditNote(internalReference);

        for(int i=0; i<=coupons.size(); i++){
            TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(coupons.get(i).getIdTypeVoucher()).get();
            amoutToDebit += typeVoucher.getAmount();
        }
        Station station = iStationService.getByInternalReference(creditNote.getIdStation()).get();
        station.setBalance(station.getBalance()-amoutToDebit);
        station.setUpdateAt(LocalDateTime.now());
        iStationService.createStation(station);

        responseCreditNoteDTO.setCoupon(coupons);
        responseCreditNoteDTO.setStation(station);
        responseCreditNoteDTO.setInternalReference(creditNote.getInternalReference());
        responseCreditNoteDTO.setStatus(creditNote.getStatus());
        responseCreditNoteDTO.setId(creditNote.getId());
        responseCreditNoteDTO.setUpdateAt(creditNote.getUpdateAt());
        responseCreditNoteDTO.setCreatedAt(creditNote.getCreatedAt());


        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("creditnote", creditNote.getInternalReference()+" - "+amoutToDebit+" FCFA - ");
        emailProps.put("station", station);
        emailProps.put("amout", amoutToDebit);

        List<Users> usersList = iUserService.getUsers();

        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_STATION) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_VALID_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_VALID_CREDIT_NOTE));
            }
            if (user.getTypeAccount().getName() == ETypeAccount.TREASURY) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_VALID_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_VALID_CREDIT_NOTE));
            }
        }

        return ResponseEntity.ok(responseCreditNoteDTO);
    }



    @Operation(summary = "Supprimer une Note de crédit", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", description = "credit note deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteRequestOpposition(@PathVariable Long internalReference) {
        CreditNote creditNote = iCreditNoteService.getByInternalReference(internalReference).get();
        iCreditNoteService.deleteCreditNote(creditNote);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Note de crédit", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Credit note not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllRequestOppositions(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<ResponseCreditNoteDTO> list = iCreditNoteService.getAllCreditNotes(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
    }
