package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.user.entity.Users;
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
public class ResponseNotebookDTO {

    private Long id;

    private Long internalReference;

    private String serialNumber;

    private String plage_coupon;

    private int numberCarton;

    private String nameStoreHouse;

    private String nameStoreKeeper;

    private float amountCoupon;

    private Long idStoreHouse;

    private Storehouse storehouse;

    private Long idStoreKeeper;

    private Users storeKeeper;

    private Long idCarton;

    private Carton carton;

    private Long idTypeVoucher;

    private TypeVoucher typeVoucher;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private Status status;
}
