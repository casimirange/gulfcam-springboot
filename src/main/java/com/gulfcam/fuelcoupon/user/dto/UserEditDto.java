package com.gulfcam.fuelcoupon.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class UserEditDto {

	@Email
	private String email;
	
	private String telephone;

	private String firstname;

	private String lastname;

	private String position;
}
