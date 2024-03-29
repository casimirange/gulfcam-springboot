package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateNotebookDTO {

    @Schema(description = "Numéro de série du coupon")
    @NotNull(message = "{serialNumber.required}")
    private String serialNumber;

    @Schema(description = "reference interne du magasinier")
    private Long idStoreKeeper;

    @Schema(description = "reference interne du type de bon")
    @NotNull(message = "{typeVoucher.required}")
    private Long idTypeVoucher;

    @Schema(description = "reference interne du carton")
    @NotNull(message = "{carton.required}")
    private Long idCarton;
}
