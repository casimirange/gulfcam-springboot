package com.gulfcam.fuelcoupon.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateClientDTO {

    @NotNull(message = "{completeName.required}")
    private String completeName;

    @NotNull(message = "{companyName.required}")
    private String companyName;

    private String address;

    private String phone;

    @Email(message = "{email.verified}")
    @NotNull(message = "{email.required}")
    private String email;

    private String RCCM;

    @NotNull(message = "{gulfcamAccountNumber.required}")
    private String gulfcamAccountNumber;

    @Schema(required = true, allowableValues = {"PARTICULAR", "ENTREPRISE", "ADMINISTRATION"}, description = "Type de client")
    @NotNull(message = "{typeClient.required}")
    private String typeClient;

}
