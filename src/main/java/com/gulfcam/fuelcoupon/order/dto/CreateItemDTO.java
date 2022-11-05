package com.gulfcam.fuelcoupon.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateItemDTO {

    @Schema(description = "Reference interne du type de bon")
    @NotNull(message = "{typevoucher.required}")
    private Long idTypeVoucher;

    @Schema(description = "Reference interne de l'entrepôt")
    @NotNull(message = "{store.required}")
    private Long idStoreHouse;

    @Schema(description = "quantité de carnet")
    private int quantityNotebook;

    @Schema(description = "quantiteé de carton")
    private int quantityCarton;

}
