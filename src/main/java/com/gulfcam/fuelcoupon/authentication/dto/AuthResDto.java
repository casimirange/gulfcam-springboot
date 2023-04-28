package com.gulfcam.fuelcoupon.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gulfcam.fuelcoupon.user.entity.StatusUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class AuthResDto {

	private Long userId;
	
	private String username;
	
	private String name;
	
	private String surname;

	private String email;
	
	private String tel1;
	
	private String bearerToken;
	
	private String refreshToken;
	
	private StatusUser status;

	private boolean authenticated;
	
	private List<String> roles;

	public AuthResDto(String bearerToken) {
		super();
		this.bearerToken = bearerToken;
	}



}
