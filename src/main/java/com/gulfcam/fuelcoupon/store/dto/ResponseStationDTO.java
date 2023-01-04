package com.gulfcam.fuelcoupon.store.dto;
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
public class ResponseStationDTO {

    private Long id;

    private Long internalReference;

    private String localization;

    private String designation;

    private Long idManagerStation;

    private Users managerStation;

    private String nameManagerStation;

    private int pinCode;

    private float balance;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private Status status;

}
