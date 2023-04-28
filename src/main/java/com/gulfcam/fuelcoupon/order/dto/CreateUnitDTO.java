package com.gulfcam.fuelcoupon.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateUnitDTO {

    @Schema(description = "Reference interne du type de bon")
    @NotNull(message = "{typevoucher.required}")
    private Long idTypeVoucher;

    @Schema(description = "Reference interne du magasin")
    @NotNull(message = "{store.required}")
    private Long idStore;

    @Schema(description = "quantité de carnet")
    @NotNull(message = "{qantity.required}")
    private int quantityNotebook;

}
