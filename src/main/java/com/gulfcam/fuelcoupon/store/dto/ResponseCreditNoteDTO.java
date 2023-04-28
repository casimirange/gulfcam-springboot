package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResponseCreditNoteDTO {

    private Long id;

    private Long internalReference;

    private List<Coupon> coupon;

    private Station station;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private Status status;

}
