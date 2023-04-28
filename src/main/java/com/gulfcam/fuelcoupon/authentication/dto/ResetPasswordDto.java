package com.gulfcam.fuelcoupon.authentication.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ResetPasswordDto {
    @NotNull(message = "${login.required}")
    String login;
}
