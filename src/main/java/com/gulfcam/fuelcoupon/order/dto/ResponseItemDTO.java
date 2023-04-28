package com.gulfcam.fuelcoupon.order.dto;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
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
public class ResponseItemDTO {

    private Long id;

    private Long internalReference;

    private TypeVoucher typeVoucher;

    private float amount;

    private Storehouse storeHouse;

    private int quantityNotebook;

    private int quantityCarton;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private Status status;

}
