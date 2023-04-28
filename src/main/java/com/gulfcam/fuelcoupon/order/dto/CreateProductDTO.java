package com.gulfcam.fuelcoupon.order.dto;
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
public class CreateProductDTO {

    @Schema(description = "Reference interne du type de bon")
    @NotNull(message = "{typevoucher.required}")
    private Long idTypeVoucher;

    @Schema(description = "Reference interne de la commande")
    @NotNull(message = "{order.required}")
    private Long idOrder;

    @Schema(description = "quantité de carnet")
    @NotNull(message = "{quantity.required}")
    private int quantityNotebook;

}
