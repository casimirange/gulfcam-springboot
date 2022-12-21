package com.gulfcam.fuelcoupon.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStoreGroupDTO {

    private Long typeVoucher;

    private float amount;

    private int quantityNoteBook;

}
