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

    private String companyName;
    @NotNull(message = "{address.required}")
    private String address;
    @NotNull(message = "{phone.required}")
    private String phone;

    private String email;

    private String niu;

    private String RCCM;

    @NotNull(message = "{gulfcamAccountNumber.required}")
    private String gulfcamAccountNumber;

    @Schema(required = true, allowableValues = {"PARTICULAR", "ENTREPRISE", "ADMINISTRATION"}, description = "Type de client")
    @NotNull(message = "{typeClient.required}")
    private String typeClient;

}
