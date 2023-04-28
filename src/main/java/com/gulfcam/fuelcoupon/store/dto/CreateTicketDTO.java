package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class CreateTicketDTO {

    @Schema(description = "reference interne de la requête d'opposition")
    @NotNull(message = "{IdRequestOpposition.required}")
    private Long IdRequestOpposition;

    @Schema(description = "reference interne du Coupon")
    @NotNull(message = "{coupon.required}")
    private Long idCoupon;

}
