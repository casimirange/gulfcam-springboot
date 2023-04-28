package com.gulfcam.fuelcoupon.store.dto;


import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseStockMovementDTO {

    private Long id;

    private Long internalReference;

    private Long idStore1;

    private Long idStoreHouse1;

    private Long idStore2;

    private Long idStoreHouse2;

    private Store store1;

    private Storehouse storeHouse1;

    private Store store2;

    private Storehouse storeHouse2;

    private String nameStore1;

    private String nameStoreHouse1;

    private String nameStore2;

    private String nameStoreHouse2;

    private String type;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

}
