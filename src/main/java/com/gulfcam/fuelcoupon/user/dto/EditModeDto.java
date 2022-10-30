package com.gulfcam.fuelcoupon.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EditModeDto {
	
	@NotNull
	private String modeName;
	
	@NotNull
	private float modePrice;
	
	private String modeDescription;
}
