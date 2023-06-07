package com.gulfcam.fuelcoupon.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateTypeVoucherDTO {

    @Schema(description = "designation")
    @NotNull(message = "{designation.required}")
    private String designation;

    @Schema(description = "Valeur monétaire (1000, 5000, 3000)")
    @NotNull(message = "{amount.required}")
    private String amount;
}
