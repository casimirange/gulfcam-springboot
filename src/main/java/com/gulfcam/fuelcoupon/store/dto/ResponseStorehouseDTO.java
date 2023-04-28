package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class ResponseStorehouseDTO {

    private Long id;

    private Long internalReference;

    private Store store;

    private String type;

    private String localisationStore;

    private String name;

    private LocalDate updateAt;

    private LocalDate createAt;

    private Status status;

}
