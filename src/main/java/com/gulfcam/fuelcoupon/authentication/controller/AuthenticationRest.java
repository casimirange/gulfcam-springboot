package com.gulfcam.fuelcoupon.authentication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gulfcam.fuelcoupon.authentication.dto.*;
import com.gulfcam.fuelcoupon.authentication.service.IAuthorizationService;
import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.authentication.service.UserDetailsImpl;
import com.gulfcam.fuelcoupon.cryptage.AES;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.user.dto.*;
import com.gulfcam.fuelcoupon.user.entity.*;
import com.gulfcam.fuelcoupon.user.repository.IRoleUserRepo;
import com.gulfcam.fuelcoupon.user.repository.ITypeAccountRepository;
import com.gulfcam.fuelcoupon.user.repository.IUserRepo;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
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
import net.minidev.json.JSONObject;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@OpenAPIDefinition(info = @Info(title = "API GULFCAM V1.0", description = "Documentation de l'API", version = "1.0"))
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
    PasswordEncoder encoder;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    private IRoleUserRepo roleRepo;

    @Autowired
    ITypeAccountRepository typeAccountRepo;

    @Autowired
    IEmailService emailService;

    @Autowired
    IAuthorizationService authorizationService;


    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Value("${app.front-reset-password-page}")
    String urlResetPasswordPage;
    @Value("${app.api-confirm-account-url}")
    String urlConfirmAccount;
    @Value("${app.front-singIn}")
    String signInUrl;
    @Value("${app.api-confirm-code-url}")
    String urlConfirmCode;
    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Value("${app.key}")
    String AES_KEY;
    AESUtil aes = new AESUtil();
    JsonMapper jsonMapper = new JsonMapper();

    @Parameters(@Parameter(name = "tel", required = true))
    @Operation(summary = "Confirmation du login pour activation du compte", tags = "authentification", responses = {
            @ApiResponse(responseCode = "200", description = "Code vérifié avec succès", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
            @ApiResponse(responseCode = "404", description = "Erreur: Utilisateur inexistant", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Erreur: Identification requise / Login déjà confirmé", content = @Content(mediaType = "Application/Json")),})

    @GetMapping("/confirm-account")
    public ResponseEntity<Object> confirmUserAccount(@RequestParam String code,
                                                     @RequestParam(required = false) String email) {
        Users user;
        String newUrl = null;
        if (code.length() == 6) {
            user = userService.getByEmail(email).orElseThrow(() -> new ResourceNotFoundException(
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

    @Operation(summary = "Réinitialiser son mot de passe 2 (confirmation du code pour le web)", tags = "authentification", description = "La validation du token est requis pour le client web", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description = "Erreur dans le format de la requete", content = @Content(mediaType = "Application/Json"))})

    @GetMapping("/confirm-code")
    public ResponseEntity<Object> resetPassword(@RequestParam String code) throws Exception {
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

    @Operation(summary = "Authentifie un utilisateur", tags = "authentification", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = AuthResDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "", content = @Content(mediaType = "Application/Json"))})

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthReqDto userAuthDto) throws JsonProcessingException {

        String login = aes.decrypt(AES_KEY, userAuthDto.getLogin());
        String pwd = aes.decrypt(AES_KEY, userAuthDto.getPassword());

        Users user = new Users(login, pwd);
        if (login.contains("@")) {
            Optional<Users> user2 = userService.getByEmail(login);
            if (user2.isPresent()) {
                user = new Users(user2.get().getEmail(), pwd);
            }
        }else{
            Optional<Users> user2 = userService.getByPinCode(Integer.parseInt(login));
            if (user2.isPresent()) {
                user = new Users(user2.get().getEmail(), pwd);
            }
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        if (userPrincipal.getStatus().getName() == EStatusUser.USER_ENABLED) {
            String code = String.valueOf(jwtUtils.generateOtpCode());
            Users userUpdate = updateExistingUser(userPrincipal.getUsername(), code);
            String telephone = userUpdate.getTelephone() != null ? userUpdate.getTelephone() : String.valueOf(userUpdate.getTelephone());
            String email = userUpdate.getEmail() != null ? userUpdate.getEmail() : String.valueOf(userUpdate.getEmail());
            String bearerToken = jwtUtils.generateJwtToken(userPrincipal.getUsername(),
                    jwtUtils.getExpirationBearerToken(), jwtUtils.getSecretBearerToken(), false);

            Map<String, Object> emailProps = new HashMap<>();
            emailProps.put("code", code);
            emailProps.put("telephone", telephone);
            emailProps.put("email", email);

            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, email, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_OPT, ApplicationConstant.TEMPLATE_EMAIL_ENTREPRISE_MEMBRE));
            log.info("Email  send successfull for user: " + email);
            log.info("Code OTP : " + code);
            System.out.println(user);

//            log.info("crypté " + aes.encrypt(userAuthDto));
            SignInResponse signInResponse = new SignInResponse(true, messageSource.getMessage("messages.code-otp", null, LocaleContextHolder.getLocale()), bearerToken, false);
//            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
//            Object json = objectWriter.writeValueAsString(signInResponse);
            jsonMapper.registerModule(new JavaTimeModule());
            Object json = jsonMapper.writeValueAsString(signInResponse);
            JSONObject cr = aes.encryptObject( AES_KEY, json);

            log.info("autre " + user);

            return ResponseEntity.ok().body(cr);
//            return ResponseEntity.ok().body(cr);


        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new MessageResponseDto(HttpStatus.UNAUTHORIZED, messageSource.getMessage("messages.email-adresse-not-verify", null, LocaleContextHolder.getLocale())));
    }


    @Parameters(value = {
            @Parameter(name = "typeAccount", schema = @Schema(allowableValues = {"STORE_KEEPER", "MANAGER_COUPON", "MANAGER_SPACES_2", "COMPTABLE", "DSI_AUDIT", "MANAGER_SPACES_1", "COMMERCIAL_ATTACHE", "SALES_MANAGER", "MANAGER_STORE", "MANAGER_ORDER", "TREASURY", "CUSTOMER_SERVICE", "MANAGER_STATION", "POMPIST"}))})
    @Operation(summary = "Inscription sur l'application", tags = "authentification", responses = {
            @ApiResponse(responseCode = "201", description = "Utilisateur crée avec succès", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
            @ApiResponse(responseCode = "400", description = "Erreur: Ce nom d'utilisateur est déjà utilisé/Erreur: Cet email est déjà utilisé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/sign-up")
    public ResponseEntity<Object> add(@Valid @RequestBody UserReqDto userAddDto, HttpServletRequest request) throws JsonProcessingException {
        String pincode = String.valueOf(userAddDto.getPinCode());

        if (userService.existsByEmail(aes.decrypt(AES_KEY,userAddDto.getEmail()), null)) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
        }
        if (userService.existsByPinCode(Integer.parseInt(aes.decrypt(AES_KEY,userAddDto.getPinCode())))) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.pin_code_exists", null, LocaleContextHolder.getLocale())));
        }
        if (userService.existsByTelephone(aes.decrypt(AES_KEY,userAddDto.getTelephone()), null)) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.phone_exists", null, LocaleContextHolder.getLocale())));
        }
        Store store = new Store();
        if (aes.decrypt(AES_KEY,userAddDto.getIdStore().toString()) != null) {

            if(!iStoreService.getByInternalReference(Long.parseLong(aes.decrypt(AES_KEY,userAddDto.getIdStore().toString()))).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store =  iStoreService.getByInternalReference(Long.parseLong(aes.decrypt(AES_KEY,userAddDto.getIdStore()))).get();
        }

//        Users u = modelMapper.map(userAddDto, Users.class);
        Users u = new Users();
        u.setUsing2FA(true);
        u.setEmail(aes.decrypt(AES_KEY,userAddDto.getEmail()));
        u.setTelephone(aes.decrypt(AES_KEY,userAddDto.getTelephone()));
        u.setFirstName(aes.decrypt(AES_KEY,userAddDto.getFirstName()));
        u.setLastName(aes.decrypt(AES_KEY,userAddDto.getLastName()));
        Set<RoleUser> roles = new HashSet<>();
        RoleUser rolesUser = roleRepo.findByName(aes.decrypt(AES_KEY,userAddDto.getRoleName()) != null ? ERole.valueOf(aes.decrypt(AES_KEY,userAddDto.getRoleName())) : ERole.ROLE_USER).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        roles.add(rolesUser);
        u.setRoles(roles);
        TypeAccount typeAccount = typeAccountRepo.findByName(aes.decrypt(AES_KEY,userAddDto.getTypeAccount()) != null ? ETypeAccount.valueOf(aes.decrypt(AES_KEY,userAddDto.getTypeAccount())) : ETypeAccount.MANAGER_STORE).orElseThrow(()-> new ResourceNotFoundException("Type de compte not found"));
        u.setTypeAccount(typeAccount);
        u.setInternalReference(jwtUtils.generateInternalReference());
        u.setPosition(aes.decrypt(AES_KEY,userAddDto.getPosition()));
        u.setPinCode(Integer.parseInt(aes.decrypt(AES_KEY,userAddDto.getPinCode())));
        u.setPassword(encoder.encode(aes.decrypt(AES_KEY,userAddDto.getPassword())));
        u.setIdStore(Long.parseLong(aes.decrypt(AES_KEY,userAddDto.getIdStore().toString())));
        u.setCreatedDate(LocalDateTime.now());
        Users user = new Users();
        String password = null;
        Map<String, Object> userAndPasswordNotEncoded = new HashMap<>();

        userAndPasswordNotEncoded = userService.add(u);
        user = (Users) userAndPasswordNotEncoded.get("user");
        userService.editStatus(user.getUserId(), Long.valueOf(EStatusUser.USER_ENABLED.ordinal() + 1L));
        UserResDto userResDto = modelMapper.map(u, UserResDto.class);
        userResDto.setStore(store);
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("firstname", userAddDto.getFirstName());
        emailProps.put("lastname", userAddDto.getLastName());

        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, userAddDto.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_USER, ApplicationConstant.TEMPLATE_EMAIL_NEW_USER));
        log.info("Email  send successfull for user: " + userAddDto.getEmail());

        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(userResDto);
        JSONObject cr = aes.encryptObject( AES_KEY, json);
        return ResponseEntity.status(HttpStatus.CREATED).body(cr);
    }

    @Parameters(value = {
            @Parameter(name = "typeAccount", schema = @Schema(allowableValues = {"STORE_KEEPER", "MANAGER_COUPON", "MANAGER_STORE", "TREASURY", "CUSTOMER_SERVICE", "MANAGER_STATION", "POMPIST"}))})
    @Operation(summary = "Modification des information/profil de l'utilisateur", tags = "authentification", responses = {
            @ApiResponse(responseCode = "201", description = "Utilisateur modifié avec succès", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
            @ApiResponse(responseCode = "400", description = "Erreur: Ce nom d'utilisateur est déjà utilisé/Erreur: Cet email est déjà utilisé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{internalReference:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    public ResponseEntity<Object> update(@Valid @RequestBody UserModifyReqDto userModifyReqDto, @PathVariable Long internalReference) throws JsonProcessingException {

        Store store = new Store();
        if (userModifyReqDto.getIdStore() != null) {

            if(!iStoreService.getByInternalReference(userModifyReqDto.getIdStore()).isPresent())
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.store_exists", null, LocaleContextHolder.getLocale())));
            store =  iStoreService.getByInternalReference(userModifyReqDto.getIdStore()).get();
        }

        Users u = userService.getByInternalReference(internalReference);
        u.setFirstName(userModifyReqDto.getFirstName());
        u.setLastName(userModifyReqDto.getLastName());
        u.setPinCode(userModifyReqDto.getPinCode());
        Set<RoleUser> roles = new HashSet<>();
        RoleUser rolesUser = roleRepo.findByName(userModifyReqDto.getRoleName() != null ? ERole.valueOf(userModifyReqDto.getRoleName()) : ERole.ROLE_USER).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        roles.add(rolesUser);
        u.setRoles(roles);
        TypeAccount typeAccount = typeAccountRepo.findByName(userModifyReqDto.getTypeAccount() != null ? ETypeAccount.valueOf(userModifyReqDto.getTypeAccount()) : ETypeAccount.MANAGER_STORE).orElseThrow(()-> new ResourceNotFoundException("Type de compte not found"));
        u.setTypeAccount(typeAccount);
        u.setPosition(userModifyReqDto.getPosition());
        u.setIdStore(userModifyReqDto.getIdStore());
        u.setCreatedDate(LocalDateTime.now());
        Users user = new Users();
        String password = null;
        Map<String, Object> userAndPasswordNotEncoded = new HashMap<>();

        userAndPasswordNotEncoded = userService.modify(u);
        user = (Users) userAndPasswordNotEncoded.get("user");
        UserResDto userResDto = modelMapper.map(u, UserResDto.class);
        userResDto.setStore(store);
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(userResDto);
        JSONObject cr = aes.encryptObject( AES_KEY, json);
        return ResponseEntity.status(HttpStatus.CREATED).body(cr);
    }


    @Operation(summary = "Vérification du code d'authentification à 2FA", tags = "authentification", responses = {
            @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Users.class)))),
            @ApiResponse(responseCode = "400", description = "code de vérification incorrect", content = @Content(mediaType = "Application/Json"))})
    @GetMapping("/verify")
    public ResponseEntity<?> verifyCode(@NotEmpty @RequestParam(name = "code", required = true) String code) throws JsonProcessingException {
//        log.info("code otp1 : "+codes);
//        String code = aes.decrypt(AES_KEY, codes);
//        log.info("code otp2 : "+code);
        Users users = authorizationService.getUserInContextApp();
        log.info("user :"+users);
            if (code.equals(users.getOtpCode()) && ChronoUnit.MINUTES.between(users.getOtpCodeCreatedAT(), LocalDateTime.now()) < 5) {
                String bearerToken = jwtUtils.generateJwtToken(users.getEmail(),
                        jwtUtils.getExpirationBearerToken(), jwtUtils.getSecretBearerToken(), true);
                String refreshToken = jwtUtils.generateJwtToken(users.getEmail(),
                        jwtUtils.getExpirationRefreshToken(), jwtUtils.getSecretRefreshToken(), true);
                userService.editToken(users.getUserId(), refreshToken);
                List<String> roles = users.getRoles().stream().map(item -> item.getName().name())
                        .collect(Collectors.toList());
                updateExistingUser(users.getEmail(), null);
                userService.updateDateLastLoginUser(users.getUserId());
                userService.updateFistLogin(users.getUserId());
                userService.editToken(users.getUserId(), null);
                users.setOtpCode(null);
                users.setOtpCodeCreatedAT(null);
            String account = users.getTypeAccount().getName().name();
            String firstname = users.getFirstName();
            String lastname = users.getLastName();
            String idStore = users.getIdStore().toString();
            String uid = users.getInternalReference().toString();
            String id = users.getUserId().toString();
            String email = users.getEmail();
            userRepo.save(users);
            log.info("user " + users.getOtpCode() + " authenticated");
            log.info("user_otp_code" + users.getOtpCode() + "otpCodeCreatedAt:" + users.getOtpCodeCreatedAT());

            AuthSignInResDto authSignInResDto = new AuthSignInResDto(bearerToken, refreshToken, "Bearer", users, roles, account
                        , firstname, lastname, idStore, uid, id, true);
            jsonMapper.registerModule(new JavaTimeModule());
            Object json = jsonMapper.writeValueAsString(authSignInResDto);
            JSONObject cr = aes.encryptObject( AES_KEY, json);
            return ResponseEntity.ok(cr);

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
        Optional<Users> users = userService.getByEmail(resetOtpCodeDto.getEmail());
//        if (users.get().getStatus().getName().equals(EStatusUser.USER_ENABLED)) {
//            return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK, " your account has already been activated !"));
//        }
        String code = String.valueOf(jwtUtils.generateOtpCode());
        Users userUpdate = updateExistingUser(resetOtpCodeDto.getEmail(), code);
        String telephone = userUpdate.getTelephone() != null ? userUpdate.getTelephone() : String.valueOf(userUpdate.getTelephone());
        String email = userUpdate.getEmail() != null ? userUpdate.getEmail() : String.valueOf(userUpdate.getEmail());

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("code", code);
        emailProps.put("telephone", telephone);
        emailProps.put("email", email);

        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, email, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_OPT, ApplicationConstant.TEMPLATE_EMAIL_ENTREPRISE_MEMBRE));
        log.info("Email  send successfull for user: " + email);
        log.info("Code OTP : " + code);

        return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK, " Code Otp generated successful !"));
    }


    @Operation(summary = "Obtenir un nouveau token de sécurité", tags = "authentification", responses = {
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

    @Operation(summary = "Réinitialiser son mot de passe etape 1 (verification du user)", tags = "authentification", responses = {
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

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("firstname", user.getFirstName());
        emailProps.put("lastname", user.getLastName());
        emailProps.put("code", user.getEmail());
        emailProps.put("username", user.getEmail());
        emailProps.put("code", urlConfirmCode + aes.encrypt(AES_KEY, user.getTokenAuth()));

        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), "", emailProps, ApplicationConstant.SUBJECT_PASSWORD_RESET, ApplicationConstant.TEMPLATE_PASSWORD_RESET));
        log.info("Email for reset password send successfull for user: " + user.getEmail());

        return ResponseEntity.ok().body(new MessageResponseDto(HttpStatus.OK,
                messageSource.getMessage("messages.code_sent_success", null, LocaleContextHolder.getLocale())));
    }

    @Operation(summary = "Réinitialiser son mot de passe 3 enrégistrement du password(pour le web uniquement)", tags = "authentification", responses = {
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
        jsonMapper.registerModule(new JavaTimeModule());
        Object json = jsonMapper.writeValueAsString(user2);
        JSONObject cr = aes.encryptObject( AES_KEY, json);
        return ResponseEntity.ok(cr);
    }

    @Operation(summary = "modifier le password d'un utilisateur", tags = "authentification", responses = {
            @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "400", description = "Erreur: Ancien mot de passe incorrect", content = @Content(mediaType = "Application/Json"))})
//    @PreAuthorize("@authorizationService.canUpdateOwnerItem(#Long.parseLong(id), 'User')")
    @PutMapping("/user/{id}/password-update")
    public ResponseEntity<?> editPassword(@PathVariable String id, @Valid @RequestBody UserEditPasswordDto userEditPasswordDto) {
        log.info("crypté "+ id);
        log.info("décrypté "+ aes.decrypt(AES_KEY, id));
        Users user = userService.getById(Long.parseLong(aes.decrypt(AES_KEY, id)));
        if (!BCrypt.checkpw(aes.decrypt(AES_KEY, userEditPasswordDto.getOldPassword()), user.getPassword())) {
            return ResponseEntity.badRequest().body(new DefaultResponseDto("Ancien mot de passe incorrect", HttpStatus.BAD_REQUEST));
        }
        List<OldPassword> oldPasswords = user.getOldPasswords();
        for (OldPassword oldPassword : oldPasswords) {
            if (BCrypt.checkpw(aes.decrypt(AES_KEY, userEditPasswordDto.getPassword()), oldPassword.getPassword())) {
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
        String email = jwtUtils.getIdGulfcamFromJwtToken(token, secret);
        return userService.getByEmail(email).get();
    }

    private Users updateExistingUser(String email, String code) {
        Optional<Users> existingUser = userService.getByEmail(email);

        if(code != null)
            existingUser.get().setOtpCode(code);

        existingUser.get().setOtpCodeCreatedAT(LocalDateTime.now());
        return userRepo.save(existingUser.get());
    }

}
