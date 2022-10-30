package com.gulfcam.fuelcoupon.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserEditPasswordDto {
	
	@NotNull
	private String oldPassword;

	@NotNull
	private String password;
}
