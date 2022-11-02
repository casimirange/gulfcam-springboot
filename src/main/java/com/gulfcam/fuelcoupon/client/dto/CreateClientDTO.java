package com.gulfcam.fuelcoupon.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    @NotNull(message = "{typeClient.required}")
    private String typeClient;

}
