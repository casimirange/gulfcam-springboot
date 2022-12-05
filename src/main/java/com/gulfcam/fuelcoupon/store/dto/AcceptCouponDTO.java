package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AcceptCouponDTO {

    @Schema(description = "Reference interne de la station")
    @NotNull(message = "{station.required}")
    private Long idStation;

}
