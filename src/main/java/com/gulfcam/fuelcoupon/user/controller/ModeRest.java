package com.gulfcam.fuelcoupon.user.controller;


import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.user.dto.EditModeDto;
import com.gulfcam.fuelcoupon.user.dto.UserResDto;
import com.gulfcam.fuelcoupon.user.entity.Mode;
import com.gulfcam.fuelcoupon.user.service.IModeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Tag(name = "modes")
@RequestMapping("/api/v1.0/modes")
@Slf4j
public class ModeRest {

	@Autowired
	private IModeService modeService;

	@Autowired
	private ModelMapper modelMapper;

	@Operation(summary = "Création d'un plan tarifaire", tags = "modes", responses = {
			@ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
			@ApiResponse(responseCode = "400", description = "Erreur: bad request", content = @Content(mediaType = "Application/Json")), })
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<Object> add(@Valid @RequestBody EditModeDto editModeDto) {
		if (modeService.existsByName(editModeDto.getModeName(), null)) {
			return ResponseEntity.badRequest()
					.body(new MessageResponseDto(HttpStatus.BAD_REQUEST, "This name already use"));
		}
		Mode m = modelMapper.map(editModeDto, Mode.class);
		Mode mode = modeService.save(m);
		return ResponseEntity.status(HttpStatus.CREATED).body(mode);
	}

	@Operation(summary = "Recupère un plan tarifaire dans la BD à partir de son id", tags = "modes", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Mode.class)))),
			@ApiResponse(responseCode = "404", description = "Mode not found", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
	@GetMapping("/{modeId:[0-9]+}")
	public ResponseEntity<Mode> getMode(@PathVariable Long modeId) {
		Mode mode = modeService.findById(modeId);
		return ResponseEntity.ok(mode);
	}

	@Operation(summary = "Liste paginée et triée par ordre décroissant de id de tous les plans tarifaire", tags = "modes", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
	@GetMapping
	public ResponseEntity<Page<Mode>> getModes() {
		Page<Mode> modes = modeService.findAll();
		return ResponseEntity.ok(modes);
	}

	@Operation(summary = "modifie un plan tarifaire de la BD", tags = "modes", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Mode.class)))),
			@ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{modeId:[0-9]+}")
	public ResponseEntity<Object> editUser(@PathVariable Long modeId, @RequestBody @Valid EditModeDto editModeDto) {
		Mode mode = modeService.findById(modeId);
		if (modeService.existsByName(editModeDto.getModeName(), modeId)) {
			return ResponseEntity.badRequest()
					.body(new MessageResponseDto(HttpStatus.BAD_REQUEST, "This name already use"));
		}
		Mode newMode = modelMapper.map(editModeDto, Mode.class);
		Mode modeRes = modeService.editMode(mode, newMode);
		return ResponseEntity.ok(modeRes);
	}

	@Operation(summary = "Supprimer un plan tarifiare", tags = "modes", responses = {
			@ApiResponse(responseCode = "200", description = "plan tarifaire supprimé avec succès", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{modeId:[0-9]+}")
	public ResponseEntity<Object> deleteMode(@PathVariable Long modeId) {
	Mode mode = modeService.deleteMode(modeId);
	if(mode.getIsDelete() != null) {
		return ResponseEntity.ok("Mode supprimé avec succès");
	} 
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppresion");
	}

}
