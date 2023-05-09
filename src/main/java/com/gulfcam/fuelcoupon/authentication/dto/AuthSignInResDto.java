package com.gulfcam.fuelcoupon.authentication.dto;

import com.gulfcam.fuelcoupon.user.entity.Users;
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
//	private Users user;
//	private List<String> roles;
	private String user;
	private List<String> roles;
	private String account;
	private String firstname;
	private String lastname;
	private String idStore;
	private String uid;
	private String id;
	private boolean authenticated;
	public AuthSignInResDto(String bearerToken) {
		super();
		this.bearerToken = bearerToken;
	}

}
