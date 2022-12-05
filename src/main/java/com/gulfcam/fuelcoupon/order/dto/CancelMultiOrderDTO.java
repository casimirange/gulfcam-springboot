package com.gulfcam.fuelcoupon.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CancelMultiOrderDTO {

    @Schema(description = "Liste de commandes")
    private List<Long> orders ;

    @Schema(description = "Reference interne du Gestionnaire de coupons")
    @NotNull(message = "{idManagerCoupon.required}")
    private Long idManagerCoupon;

}
