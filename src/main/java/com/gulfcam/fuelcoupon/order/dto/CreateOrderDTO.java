package com.gulfcam.fuelcoupon.order.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateOrderDTO {

    @Schema(description = "Référence client")
    private String clientReference;

    @Schema(description = "Reference interne du client")
    @NotNull(message = "{client.required}")
    private Long idClient;

    @Schema(description = "Reference interne Caisse/Trésorerie")
    private Long idFund;

    @Schema(description = "Reference interne du Gestionnaire de commandes")
    @NotNull(message = "{idManagerStore.required}")
    private Long idManagerOrder;

    @Schema(description = "Reference interne du Gestionnaire de coupons")
    private Long idManagerCoupon;

    @Schema(description = "Montant global NET")
    private int NetAggregateAmount;

    @Schema(description = "Montant global TTC")
    private int TTCAggregateAmount;

    @Schema(description = "Reference interne du magasinier")
    private Long idStorekeeper;

    @Schema(description = "Taxe applicable")
    private String tax;

    @Schema(description = "Reference interne du magasin")
    @NotNull(message = "{store.required}")
    private Long idStore;

    @Schema(required = true, allowableValues = {"Mail", "Appel", "Courrier papier", "Sur site"}, description = "Canal")
    private String channel;


    @Schema(description = "Délai de livraison")
    private String deliveryTime;

    @Schema(description = "Reference interne de la méthode de paiement choisie")
    private Long IdPaymentMethod;

    private String paymentReference;

    @Schema(description = "Raison d’annulation")
    private String reasonForCancellation;

    private String description;

}
