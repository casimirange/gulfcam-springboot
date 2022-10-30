package com.gulfcam.fuelcoupon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserReqDto {

	@Email(message = "{email.verified}")
	@NotNull(message = "email.required")
	private String email;
    @NotNull(message = "{phone.required}")
	@Schema(description = "Téléphone 1 personnel du compte entreprise")
	private String tel1;
	@Schema(description = "Téléphone 2 personnel du compte entreprise")
	private String tel2;

	private String question;
	@NotNull(message = "{postalCode.required}")
	private String postalCode;
	@NotNull(message = "{postalcode3.required}")
	private String countryCode3;
	@NotNull(message = "{career.required}")
	private List<String> nameCareer = new ArrayList<>();

}
