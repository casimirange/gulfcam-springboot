package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateStoreDTO {

    @Schema(description = "localisation")
    @NotNull(message = "{localisation.required}")
    private String localization;


}
