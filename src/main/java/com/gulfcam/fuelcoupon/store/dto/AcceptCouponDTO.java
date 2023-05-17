package com.gulfcam.fuelcoupon.store.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class AcceptCouponDTO {

    @Schema(description = "Reference interne de la station")
    @NotNull(message = "{station.required}")
    private String idStation;

    @Schema(description = "Modulo du coupon")
    private String modulo;

    @Schema(description = "Date de fabrication")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

}
