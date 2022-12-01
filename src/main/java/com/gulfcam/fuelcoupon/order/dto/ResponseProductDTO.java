package com.gulfcam.fuelcoupon.order.dto;
import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
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
public class ResponseProductDTO {

    private Long id;

    private Long internalReference;

    private TypeVoucher typeVoucher;

    private Order order;

    private float amount;

    private int quantityNotebook;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;

}
