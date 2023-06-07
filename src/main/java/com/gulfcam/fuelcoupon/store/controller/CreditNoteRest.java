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
import com.gulfcam.fuelcoupon.order.dto.ProductDTO;
import com.gulfcam.fuelcoupon.order.entity.*;
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
import net.minidev.json.JSONObject;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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

    @Value("${app.numberCoupon}")
    String numberCoupon;
    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;
    SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");
    JsonMapper jsonMapper = new JsonMapper();
    AESUtil aes = new AESUtil();
    @Value("${app.key}")
    String key;
    @Operation(summary = "Valider les coupons utilisés", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = RequestOpposition.class)))),
            @ApiResponse(responseCode = "404", description = "RequestOpposition not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addCreditNote(@Valid @RequestBody CreateCreditNoteDTO createCreditNoteDTO) throws JsonProcessingException {

        Coupon coupon;
        ResponseCouponMailDTO responseCouponMailDTO;
        CreditNote creditNote = new CreditNote();
        creditNote.setInternalReference(jwtUtils.generateInternalReference());
        creditNote.setIdStation(Long.parseLong(aes.decrypt(key, createCreditNoteDTO.getIdStation())));
        creditNote.setCreatedAt(LocalDateTime.now());
        Status statusCouponUsed = iStatusRepo.findByName(EStatus.USED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.USED +  "  not found"));
        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        creditNote.setStatus(status);

        iCreditNoteService.createCreditNote(creditNote);
        List<ResponseCouponMailDTO> couponList = new ArrayList<>();
        for (int i = 0; i< createCreditNoteDTO.getSerialCoupons().size(); i++){
            coupon = iCouponService.getCouponBySerialNumber(aes.decrypt(key, createCreditNoteDTO.getSerialCoupons().get(i))).get();
            if(coupon.getStatus().equals(statusCouponUsed)){
                responseCouponMailDTO= new ResponseCouponMailDTO();
                responseCouponMailDTO.setIdTypeVoucher(iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
                responseCouponMailDTO.setInternalReference(coupon.getInternalReference());
                responseCouponMailDTO.setStatus(coupon.getStatus());
                responseCouponMailDTO.setSerialNumber(coupon.getSerialNumber());
                coupon.setIdCreditNote(creditNote.getInternalReference());
                coupon.setUpdateAt(LocalDateTime.now());
                coupon.setIdStation(Long.parseLong(aes.decrypt(key, createCreditNoteDTO.getIdStation())));
                iCouponService.createCoupon(coupon);
                couponList.add(responseCouponMailDTO);
            }

        }

        float amoutToDebit = 0;

        for(int i=0; i<couponList.size(); i++){
            TypeVoucher typeVoucher = couponList.get(i).getIdTypeVoucher();
            amoutToDebit += typeVoucher.getAmount();
        }
        Station station = iStationService.getByInternalReference(creditNote.getIdStation()).get();
        station.setBalance(station.getBalance()+amoutToDebit);
        station.setUpdateAt(LocalDateTime.now());
        iStationService.createStation(station);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("couponList", couponList);

        List<Users> usersList = iUserService.getUsers();

        for (Users user : usersList) {
            if (user.getTypeAccount().getName() == ETypeAccount.COMPTABLE) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_CREDIT_NOTE));
            }
            if (user.getTypeAccount().getName() == ETypeAccount.MANAGER_STATION) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_CREDIT_NOTE));
            }
            if (user.getTypeAccount().getName() == ETypeAccount.DSI_AUDIT) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_CREDIT_NOTE));
            }
        }

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(creditNote);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Une Note de crédit par sa reference interne", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = CreditNote.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getByInternalReference(@PathVariable String internalReference) throws JsonProcessingException {
        String cn = "";
        log.info("dépassé");
        while (cn.isEmpty()){
            cn = aes.decrypt(key, internalReference);
            log.info("ref décrypté "+cn);
        }
        if (iCreditNoteService.getByInternalReference(Long.parseLong(cn)).isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.NOT_FOUND,
                    messageSource.getMessage("messages.coupon_exists", null, LocaleContextHolder.getLocale())));
        }
        CreditNote creditNote = iCreditNoteService.getByInternalReference(Long.parseLong(cn)).get();
        ResponseCreditNoteDTO responseCreditNoteDTO = new ResponseCreditNoteDTO();

        responseCreditNoteDTO.setCoupon(iCouponService.getCouponsByIdCreditNote(Long.parseLong(cn)));
        responseCreditNoteDTO.setStation(iStationService.getByInternalReference(creditNote.getIdStation()).get());
        responseCreditNoteDTO.setInternalReference(creditNote.getInternalReference());
        responseCreditNoteDTO.setStatus(creditNote.getStatus());
        responseCreditNoteDTO.setId(creditNote.getId());
        responseCreditNoteDTO.setUpdateAt(creditNote.getUpdateAt());
        responseCreditNoteDTO.setCreatedAt(creditNote.getCreatedAt());

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(responseCreditNoteDTO);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Générer Une Note de crédit par sa reference interne", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = CreditNote.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/export/{internalReference}")
    public ResponseEntity<?> exportByInternalReference(@PathVariable String internalReference) throws JRException, IOException {

        CreditNote creditNote = iCreditNoteService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        List<Coupon> couponList = iCouponService.getCouponsByIdCreditNote(Long.parseLong(aes.decrypt(key, internalReference)));
        Station station = iStationService.getByInternalReference(creditNote.getIdStation()).get();

        Users managerStation = iUserService.getByInternalReference(station.getIdManagerStation());
        ResponseCouponMailDTO responseCouponMailDTO;
        List<ResponseCouponMailDTO> responseCouponMailDTOList = new ArrayList<>();
        float amoutToDebit = 0;
        for(int i=0; i<couponList.size(); i++){
            responseCouponMailDTO= new ResponseCouponMailDTO();
            TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference(couponList.get(i).getIdTypeVoucher()).get();
            Coupon coupon = iCouponService.getByInternalReference(couponList.get(i).getInternalReference()).get();
            amoutToDebit += typeVoucher.getAmount();

            responseCouponMailDTO.setModulo(coupon.getModulo());
            responseCouponMailDTO.setProductionDate(coupon.getProductionDate());
            responseCouponMailDTO.setStatus(coupon.getStatus());
            responseCouponMailDTO.setCreatedAt(coupon.getCreatedAt());
            responseCouponMailDTO.setInternalReference(coupon.getInternalReference());
            responseCouponMailDTO.setReference(Math.round(typeVoucher.getAmount())+"");
            responseCouponMailDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponMailDTO.setIdTypeVoucher(typeVoucher);
            responseCouponMailDTO.setAmount(Math.round(typeVoucher.getAmount())*Integer.parseInt(numberCoupon)+"");
            responseCouponMailDTOList.add(responseCouponMailDTO);
        }
        byte[] data = generateCreditNote(amoutToDebit, managerStation, creditNote, station, responseCouponMailDTOList);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=creditnote-" + creditNote.getInternalReference() + ".pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

    @Operation(summary = "Valider Une Note de crédit par sa reference interne", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = CreditNote.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/valid/{internalReference}")
    public ResponseEntity<?> validCreditNote(@PathVariable String internalReference) throws JsonProcessingException {

        CreditNote creditNote = iCreditNoteService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();

        Status statusCreditNote = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut de la commande:  "  +  EStatus.ACTIVATED +  "  not found"));
        if (creditNote.getStatus() == statusCreditNote) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.credit_note_actived", null, LocaleContextHolder.getLocale())));
        }

        ResponseCreditNoteDTO responseCreditNoteDTO = new ResponseCreditNoteDTO();
        float amoutToDebit = 0;
        creditNote.setUpdateAt(LocalDateTime.now());
        Status status = iStatusRepo.findByName(EStatus.ACTIVATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.ACTIVATED +  "  not found"));
        creditNote.setStatus(status);
        iCreditNoteService.createCreditNote(creditNote);

        List<Coupon> coupons = iCouponService.getCouponsByIdCreditNote(Long.parseLong(aes.decrypt(key, internalReference)));

        for(int i=0; i<coupons.size(); i++){
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
            if (user.getTypeAccount().getName() == ETypeAccount.DSI_AUDIT) {
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_VALID_CREDIT_NOTE+" #"+creditNote.getInternalReference(), ApplicationConstant.TEMPLATE_EMAIL_VALID_CREDIT_NOTE));
            }
        }

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(responseCreditNoteDTO);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }



    @Operation(summary = "Supprimer une Note de crédit", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", description = "credit note deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteCreditNote(@PathVariable Long internalReference) {
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
    public ResponseEntity<?> getAllCreditNotes(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                      @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                      @RequestParam(required = false, defaultValue = "id") String sort,
                                                      @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCreditNoteDTO> list = iCreditNoteService.getAllCreditNotes(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Filtre des Note de crédit", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Credit note not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filtres(                 @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)            @RequestParam(required = false, value = "date") LocalDate date,
                                                      @RequestParam(required = false, value = "status") String status,
                                                      @RequestParam(required = false, value = "station") String stationName,
                                                      @RequestParam(required = false, value = "internalRef") String ref,
                                                      @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                      @RequestParam(required = false, defaultValue = "id") String sort,
                                                      @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCreditNoteDTO> list = iCreditNoteService.filtres(stationName, status, ref, date, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Note de crédit par station", tags = "Note de crédit", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Credit note not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/station/{idStation}")
    public ResponseEntity<?> getCreditNotesByIdStation(@PathVariable String idStation,@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)            @RequestParam(required = false, value = "date") LocalDate date,
                                                       @RequestParam(required = false, value = "status") String status,
                                                       @RequestParam(required = false, value = "internalRef") String ref,
                                                       @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                      @RequestParam(required = false, defaultValue = "id") String sort,
                                                      @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseCreditNoteDTO> list = iCreditNoteService.getCreditNotesByIdStation(Long.parseLong(aes.decrypt(key, idStation)), status, ref, date, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }


    private byte[] generateCreditNote(float amountToDebit, Users managerStation, CreditNote creditNote, Station station, List<ResponseCouponMailDTO> couponList) throws JRException, IOException {

        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("coupons", couponList);
        parameters.put("amountToDebit", amountToDebit+"");
        parameters.put("dateCreditNote", dateFor.format(Date.from((creditNote.getCreatedAt() == null) ? creditNote.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  creditNote.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("localization", station.getLocalization());
        parameters.put("designation", station.getDesignation());
        parameters.put("balance", station.getBalance()+"");
        parameters.put("nom", (managerStation != null) ? managerStation.getFirstName()+" " +managerStation.getLastName() : "");
        parameters.put("phone", (managerStation != null) ? managerStation.getTelephone()+"" : "");
        parameters.put("email", (managerStation != null) ? managerStation.getEmail()+"" : "");
        parameters.put("referenceManagerStation", (managerStation != null) ? managerStation.getInternalReference()+"" : "");
        parameters.put("reference", creditNote.getInternalReference()+"");
        parameters.put("the_date", dateFor.format(new Date()).toString());
        parameters.put("dateCredit", dateFor.format(Date.from(creditNote.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        Resource resourceLogo = appContext.getResource("classpath:/templates/logo.jpeg");
        InputStream inputStreamLogo = resourceLogo.getInputStream();
        parameters.put("logo", inputStreamLogo);
        /* read jrxl fille and creat jasperdesign object*/
        Resource resource = appContext.getResource("classpath:/templates/jasper/creditnote.jrxml");
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
