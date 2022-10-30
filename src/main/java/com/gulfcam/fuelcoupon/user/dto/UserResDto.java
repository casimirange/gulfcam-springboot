package com.gulfcam.fuelcoupon.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gulfcam.fuelcoupon.user.entity.RoleUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(value = Include.NON_NULL)
public class UserResDto{
	
	private Long userId;
	
	private String idJobEtrouve;

	private String email;
	
	private String tel1;

	private String tel2;

	private List<RoleUser> roles;


}
