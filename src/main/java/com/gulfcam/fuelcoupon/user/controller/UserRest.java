package com.gulfcam.fuelcoupon.user.controller;

import com.gulfcam.fuelcoupon.authentication.dto.MessageResponseDto;
import com.gulfcam.fuelcoupon.user.dto.ResponseUsersDTO;
import com.gulfcam.fuelcoupon.user.dto.UserResDto;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "users")
@RequestMapping("/api/v1.0/users")
@Slf4j
public class UserRest {
	@Autowired
	private IUserService userService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ResourceBundleMessageSource messageSource;

	@Operation(summary = "Recupérer un utilisateur général  à partir de son id", tags = "users", responses = {
			@ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
	@GetMapping("/{userId:[0-9]+}")
	@PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
	public ResponseEntity<?> getByIds(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.getById(userId));
	}

          @Operation(summary = "Liste de utilisateurs", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
          @GetMapping("")
		  @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
          public ResponseEntity<Page<ResponseUsersDTO>> get20Users(
                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                  @RequestParam(required = false, value = "size", defaultValue = "20") String sizeParam,
                  @RequestParam(required = false, defaultValue = "userId") String sort,
                  @RequestParam(required = false, defaultValue = "desc") String order) {
              Page<ResponseUsersDTO> users = userService.getUsers(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
              return ResponseEntity.ok(users);
          }

	@Operation(summary = "modifie le statut d'un compte ", tags = "users", responses = {
			@ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))), })
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@PutMapping("/{id:[0-9]+}/status")
	public ResponseEntity<Users> editStatus(@PathVariable("id") Long userId, @RequestParam Long statusId) {
		Users user = userService.editStatus(userId, statusId);
		return ResponseEntity.ok(user);
	}

	/*
          @Operation(summary = "Récupère un utilisateur à partir de son username", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })

          @GetMapping("/username")
          public ResponseEntity<User> getUserByUsername(
                  @RequestBody ParamSearchDto paramSearchDto,
                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                  @RequestParam(required = false, value = "size", defaultValue = "20") String sizeParam,
                  @RequestParam(required = false, defaultValue = "userId") String sort,
                  @RequestParam(required = false, defaultValue = "desc") String order) {
                  User user = userService.getByUsername(paramSearchDto.getParam());
                  return ResponseEntity.ok(user);
          }

          @Operation(summary = "modifier les informations d'un utilisateur", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
                  @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })

          @PreAuthorize("@authorizationService.canUpdateOwnerItem(#userId, 'User')")
          @PutMapping("/{userId:[0-9]+}")
          public ResponseEntity<Object> editUser(@PathVariable Long userId, @RequestBody @Valid UserEditDto userEditDto) {
              User user2 = userService.getById(userId);
              if (userService.existsByEmail(userEditDto.getEmail(), userId)) {
                  return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                          messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
              }
              if (userService.existsByTel(userEditDto.getTel1(), userId)
                      || userService.existsByTel(userEditDto.getTel2(), userId)) {
                  return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                          messageSource.getMessage("messages.telephone_exists", null, LocaleContextHolder.getLocale())));
              }
              if (user2.getClass().isAssignableFrom(Particular.class)) {
                  Particular particular = modelMapper.map(userEditDto, Particular.class);
                  particular.setEmail(userEditDto.getEmail());
                  particular.setTel1(userEditDto.getTel1());
                  particular.setTel2(userEditDto.getTel2());
                  Particular particular2 = particularService.editUser(userId, particular);
                  return ResponseEntity.ok(particular2);
              }
              Entreprise entreprise = modelMapper.map(userEditDto, Entreprise.class);
              entreprise.setEmail(userEditDto.getEmail());
              entreprise.setTel1(userEditDto.getTel1());
              entreprise.setTel2(userEditDto.getTel2());
              List<ActivityCategory> activitiesCat = userEditDto.getActivityCatIds() != null
                      ? activityCatRepo.findAllById(userEditDto.getActivityCatIds())
                      : new ArrayList<>();
              if (!activitiesCat.isEmpty()) {
                  entreprise.setActivityCategories(activitiesCat);
              }
              if (userEditDto.getCountry() != null || userEditDto.getTown() != null || userEditDto.getQuater() != null
                      || userEditDto.getStreet() != null || userEditDto.getPostalCode() != 0) {
                  entreprise.setAddress(new Address(userEditDto.getCountry(), userEditDto.getTown(), userEditDto.getQuater(),
                          userEditDto.getStreet(), userEditDto.getPostalCode()));
              }
              Entreprise entreprise2 = entrepriseService.editUser(userId, entreprise);
              return ResponseEntity.ok(entreprise2);
          }

          @Operation(summary = "modifie le username d'un utilisateur ", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
                  @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "400", description = "Bad request: Ce username existe déjà", content = @Content(mediaType = "Application/Json")) })
          @PreAuthorize("@authorizationService.canUpdateOwnerItem(#userId, 'User')")
          @PutMapping("/{userId:[0-9]+}/username")
          public ResponseEntity<Object> editUsername(@PathVariable Long userId, @RequestParam String username) {
              if (userService.existsByUsername(username, userId)) {
                  return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                          messageSource.getMessage("messages.username_exists", null, LocaleContextHolder.getLocale())));
              }
              User user = userService.editUsername(userId, username);
              UserResDto userResDto = modelMapper.map(user, UserResDto.class);
              return ResponseEntity.ok(userResDto);
          }

          @Operation(summary = "Modifier profil d'un compte", tags = "users", responses = {
                  @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
                  @ApiResponse(responseCode = "404", description = "Erreur: Profil not found", content = @Content(mediaType = "Application/Json")), })
          // @PreAuthorize("hasRole('ADMIN')")
          @PutMapping("/{userId:[0-9]+}/profil")
          public ResponseEntity<User> editProfil(@PathVariable Long userId, @RequestParam Long updateRoleId,
                                                 @RequestParam Long addRoleId) {
              User user = userService.getById(userId);
              User user2 = userService.editProfil(user, updateRoleId, addRoleId);
              return ResponseEntity.ok(user2);
          }

          @Operation(summary = "modifie le statut d'un compte ", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))), })
          @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
          @PutMapping("/{id:[0-9]+}/status")
          public ResponseEntity<User> editStatus(@PathVariable("id") Long userId, @RequestParam Long statusId) {
              User user = userService.editStatus(userId, statusId);
              return ResponseEntity.ok(user);
          }

          @Operation(summary = "modifie l'email d'un utilisateur ", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))), })
          @PreAuthorize("@authorizationService.canUpdateOwnerItem(#userId, 'User')")
          @PutMapping("/{userId:[0-9]+}/email")
          public ResponseEntity<?> editEmail(@PathVariable Long userId, @RequestParam String email) {
              if (userService.existsByEmail(email, userId)) {
                  return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                          messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
              }
              User user = userService.editEmail(userId, email);
              UserResDto userResDto = modelMapper.map(user, UserResDto.class);
              return ResponseEntity.ok(userResDto);
          }

          @Operation(summary = "modifie le password d'un utilisateur", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "400", description = "Erreur: Ancien mot de passe incorrect", content = @Content(mediaType = "Application/Json")) })
          @PreAuthorize("@authorizationService.canUpdateOwnerItem(#userId, 'User')")
          @PutMapping("/{userId:[0-9]+}/password-update")
          public ResponseEntity<Object> editPassword(@PathVariable Long userId,
                                                     @Valid @RequestBody UserEditPasswordDto userEditPasswordDto) {
              User user = userService.getById(userId);
              if (!BCrypt.checkpw(userEditPasswordDto.getOldPassword(), user.getPassword())) {
                  return ResponseEntity.badRequest().body("Ancien mot de passe incorrect");
              }
              List<OldPassword> oldPasswords = user.getOldPasswords();
              for (OldPassword oldPassword : oldPasswords) {
                  if (BCrypt.checkpw(userEditPasswordDto.getPassword(), oldPassword.getPassword())) {
                      return ResponseEntity.badRequest().body("Mot de passe déjà utilisé par le passé");
                  }
              }
              String response = userService.editPassword(user, userEditPasswordDto);
              return ResponseEntity.ok(response);
          }

          @Operation(summary = "Supprimer un utilisateur", tags = "users", responses = {
                  @ApiResponse(responseCode = "200", description = "utilisateur supprimé avec succès", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
                  @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
          @PreAuthorize("hasRole('ADMIN')")
          @DeleteMapping("/{userId:[0-9]+}")
          public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
              userService.deleteUser(userId); // TODO Configure hibernate envers afin qu'il insère les données supprimées et
                                              // customiser revInfo afin qu'il insère l'auteur des actions
              return ResponseEntity.ok(null);
          }

          @Hidden
          @Operation(summary = "Enregistrer une vue d'un profil", tags = "users", responses = {
                  @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = VueProfile.class)))),
                  @ApiResponse(responseCode = "400", description = "Error: this vue is already saved", content = @Content(mediaType = "Application/Json")), })
          @PostMapping("/vues")
          public ResponseEntity<Object> add(@Valid @RequestBody VueProfileDto vueProfileDto, HttpServletRequest request) {
              Optional<VueProfile> vue = vueProfilRepo.findByConsultedAndConsultant(vueProfileDto.getConsulted(),
                      vueProfileDto.getConsultant());
              if (vueProfileDto.getConsulted().equals(vueProfileDto.getConsultant()) || vue.isPresent()) {
                  return ResponseEntity.badRequest()
                          .body(new MessageResponseDto(HttpStatus.BAD_REQUEST, "Error: this vue can't be saved"));
              }
              VueProfile vueProf = modelMapper.map(vueProfileDto, VueProfile.class);
              VueProfile vueProfile = vueProfilRepo.save(vueProf);
              User user = userService.getById(vueProfile.getConsulted().getUserId());
              Notification notification = new Notification();
              notification.setVueProfile(vueProfile);
              notification.setUser(user);
              Notification notifSaved = notificationService.save(notification);
              String response = notificationService.sendNotification(notifSaved, request);
              return ResponseEntity.ok(response);
          }

          @Hidden
          @PutMapping
          public ResponseEntity<Object> editPermission(@PathVariable Long permissionId, @RequestParam boolean value) {
              Permission permission = permissionRepo.findById(permissionId)
                      .orElseThrow(() -> new ResourceNotFoundException("Permission Id " + permissionId + " not found"));
              permission.setEnabled(value);
              Permission perm = permissionRepo.save(permission);
              return ResponseEntity.ok(perm);
          }
      */

	@Operation(summary = "Modifier role d'un compte", tags = "users", responses = {
			@ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
			@ApiResponse(responseCode = "404", description = "Erreur: Role not found", content = @Content(mediaType = "Application/Json")), })
	// @PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{userId:[0-9]+}/role")
	public ResponseEntity<Users> editProfil(@PathVariable Long userId, @RequestParam Long updateRoleId,
										   @RequestParam Long addRoleId) {
		Users user = userService.getById(userId);
		Users user2 = userService.editProfil(user, updateRoleId, addRoleId);
		return ResponseEntity.ok(user2);
	}

	@Operation(summary = "modifie l'email d'un utilisateur ", tags = "users", responses = {
			@ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))), })
	@PreAuthorize("@authorizationService.canUpdateOwnerItem(#userId, 'User')")
	@PutMapping("/{userId:[0-9]+}/email")
	public ResponseEntity<?> editEmail(@PathVariable Long userId, @RequestParam String email) {
		if (userService.existsByEmail(email, userId)) {
			return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
		}
		Users user = userService.editEmail(userId, email);
		UserResDto userResDto = modelMapper.map(user, UserResDto.class);
		return ResponseEntity.ok(userResDto);
	}

	@Operation(summary = "Supprimer un utilisateur", tags = "users", responses = {
			@ApiResponse(responseCode = "200", description = "utilisateur supprimé avec succès", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId:[0-9]+}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId); // TODO Configure hibernate envers afin qu'il insère les données supprimées et
		// customiser revInfo afin qu'il insère l'auteur des actions
		return ResponseEntity.ok(null);
	}

	@Operation(summary = "Bloquer ou activer le compte d'un utilisateur", tags = "users", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
			@ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = "Application/Json")), })
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('AGENT')")
	@GetMapping("/lockAndUnlockAccount/{id}/{status}")
	public ResponseEntity<?> downloadFile(@PathVariable("id") Long userId,@PathVariable boolean status)  {
		 userService.lockAndUnlockUsers(userId,status);
		return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto(HttpStatus.OK, messageSource
				.getMessage("messages.user_status_account_update", null, LocaleContextHolder.getLocale())));
	}

}
