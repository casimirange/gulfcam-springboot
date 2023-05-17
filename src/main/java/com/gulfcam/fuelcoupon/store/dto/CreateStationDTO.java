package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateStationDTO {

    @Schema(description = "Localisation")
    private String localization;

    @Schema(description = "Dénomination")
    private String designation;

    @Schema(description = "Reférence interne du gérant de la station")
    private String managerStagion;

    @Schema(description = "Code PIN")
    @NotNull(message = "{pincode.required}")
    private String pinCode;

    @Schema(description = "Solde")
    private String balance;

}
