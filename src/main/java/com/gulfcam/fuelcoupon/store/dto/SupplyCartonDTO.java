package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SupplyCartonDTO {

    @Schema(description = "reference interne du gestionnaire espace 1")
    private String idCarton;

    @Schema(description = "reference interne de l'entrepôt de stockage")
    @NotNull(message = "{storehouse.required}")
    private String idStoreHouseSell;

}
