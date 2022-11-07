package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCartonDTO {

    @Schema(description = "Numéro de série du carton")
    @NotNull(message = "{serialNumber.required}")
    private String serialNumber;

    @Schema(description = "reference interne du magasinier")
    @NotNull(message = "{storekeeper.required}")
    private Long idStoreKeeper;

    @Schema(description = "reference interne de l'entrepôt")
    @NotNull(message = "{storehouse.required}")
    private Long idStoreHouse;
}
