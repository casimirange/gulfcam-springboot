package com.gulfcam.fuelcoupon.authentication.controller;

import com.gulfcam.fuelcoupon.authentication.dto.*;
import com.gulfcam.fuelcoupon.authentication.service.IAuthorizationService;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.authentication.service.UserDetailsImpl;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.user.dto.UserEditPasswordDto;
import com.gulfcam.fuelcoupon.user.dto.UserResDto;
import com.gulfcam.fuelcoupon.user.dto.UserResetPassword;
import com.gulfcam.fuelcoupon.user.entity.EStatusUser;
import com.gulfcam.fuelcoupon.user.entity.Mode;
import com.gulfcam.fuelcoupon.user.entity.OldPassword;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.repository.IUserRepo;
import com.gulfcam.fuelcoupon.user.service.IModeService;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.info.Info;
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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@OpenAPIDefinition(info = @Info(title = "API JOBETROUVE V2.0", description = "Documentation de l'API", version = "1.0"))
@RestController
@Tag(name = "authentification")
@RequestMapping("/api/v1.0/auth")
@Slf4j
public class AuthenticationRest {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    IAuthorizationService authorizationService;

    @Autowired
    IModeService modeService;


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Value("${app.front-reset-password-page}")
    String urlResetPasswordPage;
    @Value("${app.api-confirm-account-url}")
    String urlConfirmAccount;
    @Value("${app.link-redirect-to-payment}")
    String urlPayementRedirect;
    @Value("${app.front-singIn}")
    String signInUrl;
    @Value("${app.api-confirm-code-url}")
    String urlConfirmCode;

    @Value("${app.front.base-url}")
    private String baseUrlFront;


    @Parameters(@Parameter(name = "tel", required = true))
    @Operation(summary = "Confirmation du login pour activation du compte", tags = "users", responses = {
            @ApiResponse(responseCode = "200", description = "Code vérifié avec succès", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
            @ApiResponse(responseCode = "404", description = "Erreur: Utilisateur inexistant", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Erreur: Identification requise / Login déjà confirmé", content = @Content(mediaType = "Application/Json")),})

    @GetMapping("/confirm-account")
    public ResponseEntity<Object> confirmUserAccount(@RequestParam String code,
                                                     @RequestParam(required = false) String tel) {
        Users user;
        String newUrl = null;
        if (code.length() == 6) {
            user = userService.getByTel(tel).orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
            if (ChronoUnit.HOURS.between(user.getOtpCodeCreatedAT(), LocalDateTime.now()) > 1) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(messageSource.getMessage("messages.code_expired", null, LocaleContextHolder.getLocale()));
            }
        } else {
            try {
                user = getUser(code, jwtUtils.getSecretBearerToken());
            } catch (ExpiredJwtException e) {
                log.error("JWT token is expired: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(messageSource.getMessage("messages.code_expired", null, LocaleContextHolder.getLocale()));
            }
        }
        if ((code.equals(user.getTokenAuth()) || code.equals(user.getOtpCode()))
                && user.getStatus().getName() == EStatusUser.USER_DISABLED) {
            userService.editToken(user.getUserId(), null);
            user.setOtpCode(null);
            user.setOtpCodeCreatedAT(null);
            userRepo.save(user);

            userService.editStatus(user.getUserId(), Long.valueOf(EStatusUser.USER_ENABLED.ordinal() + 1L));

            newUrl = signInUrl ;
        } else {
            newUrl = signInUrl;
        }
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, newUrl).build();
    }


    @Parameters(@Parameter(name = "code", required = true))
    @Operation(summary = "Validation du token et redirection de l'utilisateur vers BANBORA pour le paiement", tags = "users", responses = {
            @ApiResponse(responseCode = "200", description = "Code vérifié avec succès", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
            @ApiResponse(responseCode = "404", description = "Erreur: Utilisateur inexistant", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Erreur: Identification requise / Login déjà confirmé", content = @Content(mediaType = "Application/Json")),})

    @GetMapping("/payement-redirect")
    public ResponseEntity<Object> confirmUserAccount(@RequestParam String code) {
        Users user;
        String newUrl = null;
        try {
            user = getUser(code, jwtUtils.getSecretBearerToken());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(messageSource.getMessage("messages.code_expired", null, LocaleContextHolder.getLocale()));
        }
        Mode mode = modeService.findById(1L);
        newUrl = "";
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, newUrl).build();
    }

    @Operation(summary = "Authentifie un utilisateur", tags = "authentification", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = AuthResDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "", content = @Content(mediaType = "Application/Json"))})

    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody AuthReqDto userAuthDto) throws Exception {
        Users user = new Users(userAuthDto.getLogin(), userAuthDto.getPassword());
        if (userAuthDto.getLogin().contains("@")) {
            Optional<Users> user2 = userService.getByEmail(userAuthDto.getLogin());
            if (user2.isPresent()) {
                user = new Users(user2.get().getIdGulfcam(), userAuthDto.getPassword());
            }
        }

        Optional<Users> user2 = userService.getByTel(userAuthDto.getLogin());
        if (user2.isPresent()) {
            user = new Users(user2.get().getIdGulfcam(), userAuthDto.getPassword());
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getIdGulfcam(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        if (userPrincipal.getStatus().getName() == EStatusUser.USER_ENABLED) {
            boolean isusing2FA = userPrincipal.isUsing2FA();
            if (isusing2FA == false) {
                String refreshToken = null;
                if (userPrincipal.getToken() != null) {
                    try {
                        if (jwtUtils.validateJwtToken(userPrincipal.getToken(), jwtUtils.getSecretRefreshToken())) {
                            refreshToken = userPrincipal.getToken();
                        }
                    } catch (ExpiredJwtException e) {
                        refreshToken = jwtUtils.generateJwtToken(userPrincipal.getUsername(),
                                jwtUtils.getExpirationRefreshToken(), jwtUtils.getSecretRefreshToken(), true);
                        userService.editToken(userPrincipal.getId(), refreshToken);
                    }
                } else {
                    refreshToken = jwtUtils.generateJwtToken(userPrincipal.getUsername(),
                            jwtUtils.getExpirationRefreshToken(), jwtUtils.getSecretRefreshToken(), true);
                    userService.editToken(userPrincipal.getId(), refreshToken);
                }
                String bearerToken = jwtUtils.generateJwtToken(userPrincipal.getUsername(),
                        jwtUtils.getExpirationBearerToken(), jwtUtils.getSecretBearerToken(), true);
                List<String> roles = userPrincipal.getRoles().stream().map(item -> item.getAuthority())
                        .collect(Collectors.toList());
                log.info("user " + userPrincipal.getUsername() + " authenticated");
                AuthResDto authResDto = modelMapper.map(userService.getById(userPrincipal.getId()), AuthResDto.class);
                authResDto.setBearerToken(bearerToken);
                authResDto.setRefreshToken(refreshToken);
                authResDto.setRoles(roles);
                authResDto.setAuthenticated(true);
                userService.updateDateLastLoginUser(userPrincipal.getId());
                userService.updateFistLogin(userPrincipal.getId());
                return ResponseEntity.ok(authResDto);
            } else {
                String code = String.valueOf(jwtUtils.generateOtpCode());
                Users userUpdate = updateExistingUser(userPrincipal.getUsername(), code);
                String tel = userUpdate.getTelephone() != null ? userUpdate.getTelephone() : String.valueOf(userUpdate.getTelephone());
                String bearerToken = jwtUtils.generateJwtToken(userPrincipal.getUsername(),
                        jwtUtils.getExpirationBearerToken(), jwtUtils.getSecretBearerToken(), false);
                OtpCodeDto otpCodeDto = new OtpCodeDto();
                otpCodeDto.setCode(code);
                otpCodeDto.setTel(tel);
//                jmsTemplate.convertAndSend(ApplicationConstant.PRODUCER_SMS_OTP, otpCodeDto);
                return ResponseEntity.ok().body(new SignInResponse(true, messageSource.getMessage("messages.code-otp", null, LocaleContextHolder.getLocale()), bearerToken, false));
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.email-adresse-not-verify", null, LocaleContextHolder.getLocale())));
    }

    @Operation(summary = "Vérification du code d'authentification à 2FA", tags = "authentification", responses = {
            @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Users.class)))),
            @ApiResponse(responseCode = "400", description = "code de vérification incorrect", content = @Content(mediaType = "Application/Json"))})
    @GetMapping("/verify")
    @PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
    public ResponseEntity<?> verifyCode(@NotEmpty @RequestParam(name = "code", required = true) String code) {
        Users users = authorizationService.getUserInContextApp();
        log.info("user_otp_code" + users.getOtpCode() + "otpCodeCreatedAt:" + users.getOtpCodeCreatedAT());
        if (code.equals(users.getOtpCode()) && ChronoUnit.MINUTES.between(users.getOtpCodeCreatedAT(), LocalDateTime.now()) < 5) {
            String bearerToken = jwtUtils.generateJwtToken(users.getIdGulfcam(),
                    jwtUtils.getExpirationBearerToken(), jwtUtils.getSecretBearerToken(), true);
            String refreshToken = jwtUtils.generateJwtToken(users.getIdGulfcam(),
                    jwtUtils.getExpirationRefreshToken(), jwtUtils.getSecretRefreshToken(), true);
            userService.editToken(users.getUserId(), refreshToken);
            List<String> roles = users.getRoles().stream().map(item -> item.getName().name())
                    .collect(Collectors.toList());
            updateExistingUser(users.getIdGulfcam(), null);
            userService.updateDateLastLoginUser(users.getUserId());
            userService.updateFistLogin(users.getUserId());
            log.info("user " + users.getIdGulfcam() + " authenticated");
            return ResponseEntity.ok(new AuthSignInResDto(bearerToken, refreshToken, "Bearer", roles, true));

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body((new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.code_expired", null, LocaleContextHolder.getLocale()))));
        }

    }

    @Operation(summary = "Renvoyer le code OTP pour confirmation de compte", tags = "authentification", responses = {
            @ApiResponse(responseCode = "201", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Users.class)))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "Application/Json"))})
    @PostMapping("/resetOtpCode")
    public ResponseEntity<?> resetCodeConfirmAccount(@Valid @RequestBody ResetOtpCodeDto resetOtpCodeDto) {
        Optional<Users> users = userService.getByTel(resetOtpCodeDto.getTel());
        if (users.get().getStatus().getName().equals(EStatusUser.USER_ENABLED)) {
            return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK, " your account has already been activated !"));
        }
        String code = String.valueOf(jwtUtils.generateOtpCode());
        Users userUpdate = updateExistingUser(users.get().getIdGulfcam(), code);
        String tel1 = userUpdate.getTelephone() != null ? userUpdate.getTelephone() : String.valueOf(userUpdate.getTelephone());
        OtpCodeDto otpCodeDto = new OtpCodeDto();
        otpCodeDto.setCode(code);
        otpCodeDto.setTel(tel1);
//        jmsTemplate.convertAndSend(ApplicationConstant.PRODUCER_SMS_OTP, otpCodeDto);
        return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK, " Code Otp generated successful !"));
    }


    @Operation(summary = "Obtenir un nouveau token de sécurité", tags = "users", responses = {
            @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Refresh token revoqué", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description = "Erreur dans le format de la requete", content = @Content(mediaType = "Application/Json"))})

    @GetMapping("/refresh")
    public ResponseEntity<Object> refreshUserToken(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String refreshToken) throws Exception {
        String token = jwtUtils.parseJwt(refreshToken);
        Users user = getUser(token, jwtUtils.getSecretRefreshToken());
        if (token.equals(user.getTokenAuth())) {
            String newBearerToken = jwtUtils.refreshToken(token);
            return ResponseEntity.ok(new MessageResponseDto(newBearerToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDto(
                messageSource.getMessage("messages.token_revoked", null, LocaleContextHolder.getLocale())));
    }

    @Operation(summary = "Réinitialiser son mot de passe etape 1 (verification du user)", tags = "users", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description = "Erreur dans le format de la requete", content = @Content(mediaType = "Application/Json"))})
    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto)
            throws Exception {
        if (resetPasswordDto.getLogin() == null || resetPasswordDto.getLogin().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.requete_incorrect", null, LocaleContextHolder.getLocale())));
        }
        Users user = userService.checkUserAndGenerateCode(resetPasswordDto.getLogin());
        if (resetPasswordDto.getLogin().contains("@")) {
            EmailResetPasswordDto emailVerificationDto = new EmailResetPasswordDto();
            emailVerificationDto.setCode(urlConfirmCode + user.getTokenAuth());
            emailVerificationDto.setTo(user.getEmail());
            emailVerificationDto.setUsername(user.getIdGulfcam());
            emailVerificationDto.setObject(ApplicationConstant.SUBJECT_PASSWORD_RESET);
            emailVerificationDto.setTemplate(ApplicationConstant.TEMPLATE_PASSWORD_RESET);
//            jmsTemplate.convertAndSend(ApplicationConstant.PRODUCER_EMAIL_RESET_PASSWORD, emailVerificationDto);
        } else {
            String tel1 = user.getTelephone() != null ? user.getTelephone() : String.valueOf(user.getTelephone());
            OtpCodeDto otpCodeDto = new OtpCodeDto();
            otpCodeDto.setCode(user.getOtpCode());
            otpCodeDto.setTel(tel1);
//            jmsTemplate.convertAndSend(ApplicationConstant.PRODUCER_SMS_OTP, otpCodeDto);
        }
        return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK,
                messageSource.getMessage("messages.code_sent_success", null, LocaleContextHolder.getLocale())));
    }

    @Operation(summary = "Réinitialiser son mot de passe 2 (confirmation du code pour le web)", tags = "users", description = "La validation du token est requis pour le client web", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description = "Erreur dans le format de la requete", content = @Content(mediaType = "Application/Json"))})

    @GetMapping("/confirm-code-email")
    public ResponseEntity<Object> resetPassword(@RequestParam String code, @RequestParam(required = false) String tel,
                                                @RequestBody(required = false) UserResetPassword userResetPwd) throws Exception {
        Users user;
        try {
            user = getUser(code, jwtUtils.getSecretBearerToken());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.code_expired", null, LocaleContextHolder.getLocale())));
        }
        if (code.equals(user.getTokenAuth())) {
            String newUrl = urlResetPasswordPage +"/" + code;
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, newUrl).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.unauthorized", null, LocaleContextHolder.getLocale())));
    }

    @Operation(summary = "Réinitialiser son mot de passe 2 (enrégistrement du password pour le mobile)", tags = "users", description = "Le téléphone et le nouveau password sont requis pour le client mobile", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description =  "Erreur dans le format de la requete", content = @Content(mediaType = "Application/Json"))})

    @PostMapping("/confirm-code-phone")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody UserResetPassword userResetPassword) throws Exception {
        Users user;
        if (userResetPassword.getCode().length() == 4) {
            user = userService.getByTel(userResetPassword.getTel()).orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
            if (userResetPassword.getCode().equals(user.getOtpCode()) && ChronoUnit.HOURS.between(user.getOtpCodeCreatedAT(), LocalDateTime.now()) > 10) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.code_expired", null, LocaleContextHolder.getLocale())));
            }
            if (userResetPassword.getCode().equals(user.getOtpCode())) {
                Users user2 = userService.resetPassword(user, userResetPassword.getPassword());
                return ResponseEntity.ok(new MessageResponseDto(HttpStatus.OK, messageSource.getMessage("messages.password_reset_successful", null, LocaleContextHolder.getLocale())));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.code_not_valid", null, LocaleContextHolder.getLocale())));

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.unauthorized", null, LocaleContextHolder.getLocale())));
    }


    @Operation(summary = "Réinitialiser son mot de passe 3 enrégistrement du password(pour le web uniquement)", tags = "users", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description = "Mot de passe déjà utilisé par le passé", content = @Content(mediaType = "Application/Json"))})

    @PutMapping("/reset-password")
    public ResponseEntity<Object> resetPassword2(@RequestParam String code, @RequestBody UserResetPassword userResetPwd)
            throws Exception {
        Users user = userRepo.findByTokenAuth(code).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
        List<OldPassword> oldPasswords = user.getOldPasswords();
        for (OldPassword oldPassword : oldPasswords) {
            if (BCrypt.checkpw(userResetPwd.getPassword(), oldPassword.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,messageSource.getMessage("messages.password_already_use", null,
                        LocaleContextHolder.getLocale())));
            }
        }
        Users user2 = userService.resetPassword(user, userResetPwd.getPassword());
        return ResponseEntity.ok(user2);
    }

    @Operation(summary = "modifier le password d'un utilisateur", tags = "users", responses = {
            @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description = "Erreur: Ancien mot de passe incorrect", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("@authorizationService.canUpdateOwnerItem(#id, 'User')")
    @PutMapping("/user/{id}/password-update")
    public ResponseEntity<?> editPassword(@PathVariable Long id, @Valid @RequestBody UserEditPasswordDto userEditPasswordDto) {
        Users user = userService.getById(id);
        if (!BCrypt.checkpw(userEditPasswordDto.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new DefaultResponseDto("Ancien mot de passe incorrect", HttpStatus.BAD_REQUEST));
        }
        List<OldPassword> oldPasswords = user.getOldPasswords();
        for (OldPassword oldPassword : oldPasswords) {
            if (BCrypt.checkpw(userEditPasswordDto.getPassword(), oldPassword.getPassword())) {
                return ResponseEntity.badRequest().body(new DefaultResponseDto("Mot de passe déjà utilisé par le passé", HttpStatus.BAD_REQUEST));
            }
        }
        userService.editPassword(user, userEditPasswordDto);
        userService.updateFistLogin(user.getUserId());
        return ResponseEntity.ok(new DefaultResponseDto("Mot de passe réinitialisé avec succès ", HttpStatus.OK));
    }

    @Operation(summary = "Information sur un utilisateur", tags = "authentification", responses = {
            @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Users.class)))),
            @ApiResponse(responseCode = "401", description = "", content = @Content(mediaType = "Application/Json"))})
    @GetMapping("/user/me")
    public Users getCurrentUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        String tokenauth = jwtUtils.parseJwt(token);
        Users user = getUser(tokenauth, jwtUtils.getSecretBearerToken());
        return null;
    }

    private Users getUser(String token, String secret) {
        String email = jwtUtils.getIdJobEtrouveFromJwtToken(token, secret);
        return userService.getByEmail(email).get();
    }

    private Users updateExistingUser(String email, String code) {
        Optional<Users> existingUser = userService.getByEmail(email);
        existingUser.get().setOtpCode(code);
        existingUser.get().setOtpCodeCreatedAT(LocalDateTime.now());
        return userRepo.save(existingUser.get());
    }

}
