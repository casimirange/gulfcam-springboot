package com.gulfcam.fuelcoupon.authentication.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ResetOtpCodeDto {
    @NotNull(message = "${appProvider.required}")
    private String appProvider;
    @NotNull(message = "${email.required}")
    private String email;
}
