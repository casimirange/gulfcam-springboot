package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.store.entity.Notebook;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseCouponMailDTO {

    private Long internalReference;

    private String serialNumber;

    private int modulo;

    private TypeVoucher idTypeVoucher;

    private LocalDate productionDate;

    private Status status;

    private LocalDateTime createdAt;

}
