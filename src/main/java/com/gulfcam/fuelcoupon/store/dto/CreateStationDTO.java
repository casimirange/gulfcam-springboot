package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateStationDTO {

    @Schema(description = "Localisation")
    private String localization;

    @Schema(description = "Dénomination")
    private String designation;

    @Schema(description = "Code PIN")
    @NotNull(message = "{pincode.required}")
    private int pinCode;

    @Schema(description = "Solde")
    private float balance;

}
