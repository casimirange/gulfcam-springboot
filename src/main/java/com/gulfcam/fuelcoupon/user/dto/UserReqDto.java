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
	@Schema(description = "Téléphone")
	private String telephone;
	@NotNull(message = "{pinCode.required}")
	@Schema(description = "Code pin")
	private int pinCode;

}
