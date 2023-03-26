package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreateRequestOppositionDTO {

    @Schema(description = "description")
    private String description;

    @Schema(description = "Raison")
    private String reason;

    @Schema(description = "reference interne du client)")
    @NotNull(message = "{client.required}")
    private Long idClient;

    @Schema(description = "reference interne de l'attaché commercial")
    @NotNull(message = "{serviceclient.required}")
    private Long idCommercialAttache;

    @Schema(description = "reference interne du Directeur commercial")
    private Long idSalesManager;

    @Schema(description = "Liste de coupons")
    private List<String> serialCoupons ;
}
