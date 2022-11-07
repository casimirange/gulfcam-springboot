package com.gulfcam.fuelcoupon.store.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateStoreDTO {

    @Schema(description = "localisation")
    @NotNull(message = "{localisation.required}")
    private String localization;


}
