package com.gulfcam.fuelcoupon.store.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateStockMovementDTO {



    @Schema(description = "reference interne du magasin 1")
    @NotNull(message = "{store.required}")
    private Long idStore1;

    @Schema(description = "reference interne de l'entrepôt de stockage 1")
    @NotNull(message = "{storehouse.required}")
    private Long idStoreHouseStockage1;

    @Schema(description = "reference interne du magasin 2")
    @NotNull(message = "{store.required}")
    private Long idStore2;

    @Schema(description = "reference interne du magasinier")
    private Long idStoreKeeper;

    @Schema(description = "reference interne de l'entrepôt de stockage 2")
    @NotNull(message = "{storehouse.required}")
    private Long idStoreHouseStockage2;

    @Schema(description = "Quantité de carton déplacée")
    @NotNull(message = "{quantity.required}")
    private int quantityCarton;

    @Schema(allowableValues = {"10000", "3000", "5000"}, description = "Type de Bon: exemple 10000, 5000, 3000 (ce sont les premiers chiffres en debut des numero de serie) ")
    @NotNull(message = "{typeVoucher.required}")
    private int typeVoucher;

    @Schema(description = "type (transfert, stockage, approvisionnement)", allowableValues = {"transfert", "stockage", "approvisionnement"})
    @NotNull(message = "{type.required}")
    private String type;

}
