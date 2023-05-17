package com.gulfcam.fuelcoupon.store.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreateStockMovementDTO {

    @Schema(description = "reference interne de l'entrepôt de stockage de destination")
    @NotNull(message = "{storehouse.required}")
    private String idStoreHouseStockage;

    @Schema(description = "reference interne du gestionnaire espace 1")
    private String idSpaceManager1;

    @Schema(description = "Liste de cartons a ")
    @NotNull(message = "{carton.required}")
    private List<String> listCartons ;

}
