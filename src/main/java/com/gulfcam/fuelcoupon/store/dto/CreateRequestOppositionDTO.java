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

    @Schema(description = "reference interne du service client / utilisateur")
    @NotNull(message = "{serviceclient.required}")
    private Long idServiceClient;

    @Schema(description = "reference interne du gestionnaire de coupons")
    private Long idManagerCoupon;

    @Schema(description = "Liste de coupons")
    private List<String> serialCoupons ;
}
