package com.gulfcam.fuelcoupon.store.dto;

import com.gulfcam.fuelcoupon.utilities.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseTicketDTO {

    private Long internalReference;

    private String serialNumber;

    private String nameClient;

    private Long notebookNumber;

    private LocalDateTime createdAt;

    private float amount;

    private LocalDateTime updateAt;

    private Status status;
}
