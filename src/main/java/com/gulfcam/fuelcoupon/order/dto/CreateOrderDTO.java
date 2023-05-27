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
    private String idClient;

    @Schema(description = "Reference interne Caisse/Trésorerie")
    private String idFund;

    @Schema(description = "Reference interne de l'attaché commercial")
    @NotNull(message = "{idManagerStore.required}")
    private String idCommercialAttache;

    @Schema(description = "Reference interne du Directeur commercial")
    private String idSalesManager;

    @Schema(description = "Montant global NET")
    private String NetAggregateAmount;

    @Schema(description = "Montant global TTC")
    private String TTCAggregateAmount;

    @Schema(description = "Reference interne du Gestionnaire espace 2")
    private String idSpaceManager2;

    @Schema(description = "Taxe applicable")
    private String tax;

    @Schema(description = "Reference interne du magasin")
    @NotNull(message = "{store.required}")
    private String idStore;

    @Schema(required = true, allowableValues = {"Mail", "Appel", "Courrier papier", "Sur site"}, description = "Canal")
    private String channel;


    @Schema(description = "Délai de livraison")
    private String deliveryTime;

    @Schema(description = "Reference interne de la méthode de paiement choisie")
    private String IdPaymentMethod;

    private String paymentReference;

    private String description;

}
