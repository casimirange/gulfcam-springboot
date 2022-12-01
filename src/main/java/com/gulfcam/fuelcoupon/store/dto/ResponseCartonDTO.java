package com.gulfcam.fuelcoupon.store.dto;

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
public class ResponseCartonDTO {

    private Long id;

    private Long internalReference;

    private int serialTo;

    private int number;

    private int serialFrom;

    private int typeVoucher;

    private int from;

    private int to;

    private Users storeKeeper;

    private String nameStoreHouse;

    private Storehouse storeHouse;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private Status status;
}
