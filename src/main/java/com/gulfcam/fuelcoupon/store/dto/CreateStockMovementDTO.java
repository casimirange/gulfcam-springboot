package com.gulfcam.fuelcoupon.store.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateStockMovementDTO {

    @Schema(description = "reference interne du magasin 1")
    @NotNull(message = "{store.required}")
    private Long idStore1;

    @Schema(description = "reference interne de l'entrepôt 1")
    @NotNull(message = "{storehouse.required}")
    private Long idStoreHouse1;

    @Schema(description = "reference interne du magasin 2")
    @NotNull(message = "{store.required}")
    private Long idStore2;

    @Schema(description = "reference interne de l'entrepôt 2")
    @NotNull(message = "{storehouse.required}")
    private Long idStoreHouse2;

    @Schema(description = "type (transfert, stockage, approvisionnement)", allowableValues = {"transfert", "stockage", "approvisionnement"})
    @NotNull(message = "{type.required}")
    private String type;

}
