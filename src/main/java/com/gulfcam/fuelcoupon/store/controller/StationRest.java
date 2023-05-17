package com.gulfcam.fuelcoupon.store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.store.dto.CreateStationDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStationDTO;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.repository.IStationRepo;
import com.gulfcam.fuelcoupon.store.service.IStationService;
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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "Station")
@RequestMapping("/api/v1.0/station")
@Slf4j
public class StationRest {


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;


    @Autowired
    IStationService iStationService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IStationRepo iStationRepo;

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
    @Operation(summary = "création des informations pour une Station", tags = "Station", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Station.class)))),
            @ApiResponse(responseCode = "404", description = "Station not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> addStation(@Valid @RequestBody CreateStationDTO createStationDTO) throws JsonProcessingException {

        if (iStationService.existsStationByPinCode(Integer.parseInt(aes.decrypt(key, createStationDTO.getPinCode())))) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.pincode_exists", null, LocaleContextHolder.getLocale())));
        }
        Users managerStation = new Users();

        if (createStationDTO.getManagerStagion()  != null) {
            managerStation = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createStationDTO.getManagerStagion())));

            if(managerStation.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        Station station = new Station();
        station.setInternalReference(jwtUtils.generateInternalReference());
        station.setCreatedAt(LocalDateTime.now());
        station.setPinCode(Integer.parseInt(aes.decrypt(key, createStationDTO.getPinCode())));
        station.setIdManagerStation(Long.parseLong(aes.decrypt(key, createStationDTO.getManagerStagion())));
        station.setBalance(Float.parseFloat(aes.decrypt(key, createStationDTO.getBalance())));
        station.setLocalization(aes.decrypt(key, createStationDTO.getLocalization()));
        station.setDesignation(aes.decrypt(key, createStationDTO.getDesignation()));
        Status status = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        station.setStatus(status);

        iStationService.createStation(station);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(station);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Modification des informations pour une Station", tags = "Station", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Station.class)))),
            @ApiResponse(responseCode = "404", description = "Station not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<?> updateStation(@Valid @RequestBody CreateStationDTO createStationDTO, @PathVariable String internalReference) throws JsonProcessingException {

//        if (!iStationService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).isPresent()) {
//            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                    messageSource.getMessage("messages.station_exists", null, LocaleContextHolder.getLocale())));
//        }
        Station station = iStationService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();

        Users managerStation = new Users();

        if (createStationDTO.getManagerStagion()  != null) {
            managerStation = iUserService.getByInternalReference(Long.parseLong(aes.decrypt(key, createStationDTO.getManagerStagion())));

            if(managerStation.getUserId() == null)
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.user_exists", null, LocaleContextHolder.getLocale())));
        }

        if (createStationDTO.getManagerStagion() != null)
        station.setUpdateAt(LocalDateTime.now());
        station.setPinCode(Integer.parseInt(aes.decrypt(key, createStationDTO.getPinCode())));
        station.setIdManagerStation(Long.parseLong(aes.decrypt(key, createStationDTO.getManagerStagion())));
        station.setBalance(Float.parseFloat(aes.decrypt(key, createStationDTO.getBalance())));
        station.setLocalization(aes.decrypt(key, createStationDTO.getLocalization()));
        station.setDesignation(aes.decrypt(key, createStationDTO.getDesignation()));

        iStationService.createStation(station);

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(station);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Une Station par sa reference interne", tags = "Station", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Station.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/{internalReference}")
    public ResponseEntity<?> getStationByInternalReference(@PathVariable String internalReference) throws JsonProcessingException {
        Station station = iStationService.getByInternalReference(Long.parseLong(aes.decrypt(key, internalReference))).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(station);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer Une Station par son code pin", tags = "Station", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Station.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/pincode/{pinCode}")
    public ResponseEntity<?> getStationByPinCode(@PathVariable String pinCode) throws JsonProcessingException {
        Station station = iStationService.getStationByPinCode(Integer.parseInt(aes.decrypt(key, pinCode))).get();
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(station);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Supprimer une Station", tags = "Station", responses = {
            @ApiResponse(responseCode = "200", description = "Station deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @DeleteMapping("/{internalReference:[0-9]+}")
    public ResponseEntity<Object> deleteStation(@PathVariable Long internalReference) {
        Station station = iStationService.getByInternalReference(internalReference).get();
        iStationService.deleteStation(station);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des Stations", tags = "Station", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Carton not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getAllStations(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                             @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                             @RequestParam(required = false, defaultValue = "id") String sort,
                                             @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseStationDTO> list = iStationService.getAllStations(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Recupérer la liste des station par désignation", tags = "Station", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Station.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/like/{designation}")
    public ResponseEntity<?> getStationsByDesignationContains(@PathVariable String designation) throws JsonProcessingException {
        List<Station> stationList = iStationService.getStationsByDesignationContains(designation);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(stationList);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "Filtrer les station", tags = "Station", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Station.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filtrerStations(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                            @RequestParam(required = false, value = "designation") String designation,
                                            @RequestParam(required = false, value = "localization") String localization,
                                            @RequestParam(required = false, value = "pinCode") String pinCode,
                                            @RequestParam(required = false, value = "idManagerStation") String idManagerStation,
                                            @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                            @RequestParam(required = false, defaultValue = "id") String sort,
                                            @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseStationDTO> list = iStationService.filtres(designation, localization, pinCode, idManagerStation, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(list);
        JSONObject cr = aes.encryptObject( key, json);
        return ResponseEntity.ok(cr);
    }
    }
