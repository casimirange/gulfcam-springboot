package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCartonDTO {

    @Schema(description = "debut numéro de série du carton: exemple 0000001 ou 1300001 ")
    @NotNull(message = "{serialNumber.required}")
    private int serialTo;

    @Schema(description = "Fin numéro de série du carton: exemple 1000000 ou 1450000 ")
    @NotNull(message = "{serialNumber.required}")
    private int serialFrom;

    @Schema(allowableValues = {"1", "3", "5"}, description = "Type de Bon: exemple 1, 5, 3 (ce sont les premiers chiffres en debut des numero de serie) ")
    @NotNull(message = "{typeVoucher.required}")
    private int typeVoucher;

    @Schema(description = "reference interne du magasinier")
    private Long idStoreKeeper;

    @Schema(description = "reference interne de l'entrepôt")
    @NotNull(message = "{storehouse.required}")
    private Long idStoreHouse;

    @Schema(description = "Numéro de carton: Exmple 101 ou 130 ")
    @NotNull(message = "{serialNumber.required}")
    private int number;

    @Schema(description = "debut de plage de coupon: exemple 0000001 ou 1300001 ")
    @NotNull(message = "{serialNumber.required}")
    private int to;

    @Schema(description = "Fin de plage de coupon: exemple 1000001 ou 1010000 ")
    @NotNull(message = "{serialNumber.required}")
    private int from;
}
