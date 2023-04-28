package com.gulfcam.fuelcoupon.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gulfcam.fuelcoupon.store.entity.Store;
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

	private Long internalReference;

	private int pinCode;

	private String email;

	private String telephone;

	private String lastName;

	private String firstName;

	private String position;

	private Store store;

	private List<RoleUser> roles;


}
