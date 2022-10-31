package com.gulfcam.fuelcoupon.utilities.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
import com.gulfcam.fuelcoupon.utilities.dto.*;
import com.gulfcam.fuelcoupon.utilities.entity.SettingProperties;
import com.gulfcam.fuelcoupon.utilities.service.IUtilitieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Tag(name = "Utilities")
@RequestMapping("/api/v1.0/utilities")
@Slf4j
public class UtilitiesRestController {

    @Autowired
    IUtilitieService utilitieService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    IEmailService emailService;

    @Operation(summary = "Liste complet de tous les niveaux d'expérience", tags = "utilities", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @GetMapping("/salutation")
    public ResponseEntity<?> getBonjourMessage() {
        BonjourMessage message = new BonjourMessage();
        message.setMessage("Bonjour!!");
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Création d'un parametre système", tags = "utilities", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = SettingProperties.class)))),
            @ApiResponse(responseCode = "400", description = "Erreur: bad request", content = @Content(mediaType = "Application/Json")),})
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/settingpropertie")
    public ResponseEntity<Object> add(@RequestBody SettingPropertieDto settingPropertieDto) {
        SettingProperties c = modelMapper.map(settingPropertieDto, SettingProperties.class);
        SettingProperties settingsave = utilitieService.createSetting(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(settingsave);
    }

    @Operation(summary = "modiffication d'un paramêtre système", tags = "utilities", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = SettingProperties.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settingpropertie/{settingId:[0-9]+}")
    public ResponseEntity<Object> editSetting(@PathVariable Long settingId, @RequestBody @Valid SettingPropertieDto settingPropertieDto) {
        SettingProperties c = modelMapper.map(settingPropertieDto, SettingProperties.class);
        SettingProperties settingsave = utilitieService.updateSetting(c, settingId);
        return ResponseEntity.ok(settingsave);
    }

    @Operation(summary = "Liste de tous les paramêtre système", tags = "utilities", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @GetMapping("/settingpropertie")
    public ResponseEntity<List<SettingProperties>> getAllSetting() {
        List<SettingProperties> settingProperties = utilitieService.listSttingProperties();
        return ResponseEntity.ok(settingProperties);
    }

    @Operation(summary = "Supprimer un paramêtre système", tags = "utilities", responses = {
            @ApiResponse(responseCode = "200", description = "paramêtre système supprimé avec succès", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/settingpropertie/{settingId:[0-9]+}")
    public ResponseEntity<Object> deleteSettingProperties(@PathVariable Long settingId) {
        utilitieService.deleteSettingProperties(settingId);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("request_successful-delete", null, LocaleContextHolder.getLocale())));
    }


}
