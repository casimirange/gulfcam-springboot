package com.gulfcam.fuelcoupon.user.dto;

import com.gulfcam.fuelcoupon.user.entity.ERole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserReqDto {

	@Email(message = "{email.verified}")
	@NotNull(message = "email.required")
	private String email;

	@NotNull(message = "{phone.required}")
	@Schema(description = "Téléphone")
	private String telephone;

	@NotNull(message = "{pinCode.required}")
	@Schema(description = "Code pin")
	private int pinCode;

	@NotNull(message = "{idStore.required}")
	@Schema(description = "Reference interne du magasin associé")
	private Long idStore;

	@Size(min = 6,message = "{password.lenght}")
	@NotNull(message = "{password.required}")
	@Pattern.List({
			@Pattern(regexp = "(?=.*[0-9]).+", message = "{password.number}")
			,
			@Pattern(regexp = "(?=.*[a-z]).+", message = "{password.lowercase}")
			,
			@Pattern(regexp = "(?=.*[A-Z]).+", message = "{password.upercase}")
			,
			@Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message = "{password.capitalletter}")
			,
			@Pattern(regexp = "(?=\\S+$).+", message = "{password.spacer}")})
	@Schema(description = "Mot de passe")
	private String password;

	@NotNull(message = "{firstName.required}")
	@Schema(description = "Prénom")
	private String firstName;

	@Schema(description = "Nom")
	private String lastName;

	@Schema(description = "Poste occupé")
	private String position;

	@Schema(description = "Role de l'utilisateur, defaut ROLE_USER", defaultValue = "ROLE_USER", allowableValues = {"ROLE_ADMIN", "ROLE_SUPERADMIN", "ROLE_AGENT","ROLE_PRE_VERIFICATION_USER","ROLE_USER"})
	private String roleName;

	@Schema(description = "Type de compte de l'utilisateur, defaut MANAGER_STORE", defaultValue = "MANAGER_STORE", allowableValues = {"STORE_KEEPER", "MANAGER_COUPON", "MANAGER_STORE", "TREASURY", "CUSTOMER_SERVICE", "MANAGER_STATION", "POMPIST"})
	private String typeAccount;

}
