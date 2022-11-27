package com.gulfcam.fuelcoupon.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductDTO {

    @Schema(description = "Reference interne du type de bon")
    private String price;

    @Schema(description = "quantité de carnet")
    private int quantityNotebook;

    @Schema(description = "carnet")
    private String product;

}
