package com.gulfcam.fuelcoupon.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignInResDto {

	private String bearerToken;
	private String refreshToken;
	private String tokenType;
	private List<String> roles;
	private boolean authenticated;
	public AuthSignInResDto(String bearerToken) {
		super();
		this.bearerToken = bearerToken;
	}

}
