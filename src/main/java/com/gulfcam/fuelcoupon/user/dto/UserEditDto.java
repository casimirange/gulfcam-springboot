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
	
	private String tel1;

	private String tel2;

	private String name;

	private String surname;

	private String abortUS;

	private String profileTitle;

	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate birthdate;

	private String birthdatePlace;

	@Size(max = 1)
	private String sexe;

	private String maritalStatus;

	private String nationality;

	private String residenceCountry;

	private String town;

	private String companyName;

	private Long capital;

	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate createdDate;

	private int taxPayerNumber;

	private String siteWeb;
	
	private Set<Long> activityCatIds;
	
	private int postalCode;

	private String street;
	
	private String country;
	
	private String quater;
}
