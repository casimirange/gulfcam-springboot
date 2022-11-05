package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateRequestOppositionDTO {

    @Schema(description = "description")
    private String description;

    @Schema(description = "Raison")
    private String reason;

    @Schema(description = "reference interne du service client / utilisateur")
    @NotNull(message = "{serviceclient.required}")
    private Long idServiceClient;

    @Schema(description = "reference interne du gestionnaire de coupons")
    private Long idManagerCoupon;

}
