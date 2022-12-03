package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCouponDTO {

    @Schema(description = "Numéro de série du coupon")
    @NotNull(message = "{serialNumber.required}")
    private String serialNumber;

    @Schema(description = "reference interne du client")
    private Long idClient;

    @Schema(description = "reference interne du ticket")
    private Long idTicket;

    @Schema(description = "reference interne du carnet")
    @NotNull(message = "{notebook.required}")
    private Long idNotebook;

    @Schema(description = "reference interne du type de bon")
    @NotNull(message = "{typeVoucher.required}")
    private Long idTypeVoucher;

    @Schema(description = "reference interne de la station")
    private Long idStation;

}
