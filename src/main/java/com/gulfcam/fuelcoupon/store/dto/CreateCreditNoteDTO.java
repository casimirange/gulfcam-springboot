package com.gulfcam.fuelcoupon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreateCreditNoteDTO {

    @Schema(description = "reference interne de la station")
    @NotNull(message = "{idStation.required}")
    private String idStation;

    @Schema(description = "Liste de coupons")
    private List<String> serialCoupons ;
}
