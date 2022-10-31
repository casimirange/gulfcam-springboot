package com.gulfcam.fuelcoupon.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserReqDtoImport {

    @Email(message = "{email.verified}")
    @NotNull(message = "email.required")
    private String email;
    @NotNull(message = "{phone.required}")
    private String telephone;
}
