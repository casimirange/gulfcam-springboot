package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateStorehouseDTO {

    @Schema(description = "reference interne du magasin")
    @NotNull(message = "{store.required}")
    private Long idStore;

    @Schema(description = "type")
    @NotNull(message = "{type.required}")
    private String type;

    @Schema(description = "nom de l'entreprôt")
    @NotNull(message = "{type.required}")
    private String name;

}
