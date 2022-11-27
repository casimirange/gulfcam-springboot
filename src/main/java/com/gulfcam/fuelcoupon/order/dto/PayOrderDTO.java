package com.gulfcam.fuelcoupon.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PayOrderDTO {

    @Schema(description = "Reference interne Caisse/Trésorerie")
    @NotNull(message = "{fund.required}")
    private Long idFund;

    @Schema(description = "Reference interne de la méthode de paiement choisie")
    @NotNull(message = "{paymentmethode.required}")
    private Long IdPaymentMethod;

    @Schema(description = "Reference de paiement")
    @NotNull(message = "{payment.required}")
    private String paymentReference;

}
