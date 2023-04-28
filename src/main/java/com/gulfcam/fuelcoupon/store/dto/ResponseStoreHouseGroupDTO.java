package com.gulfcam.fuelcoupon.store.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStoreHouseGroupDTO {

    private Long typeVoucher;

    private float amount;

    private int quantityNoteBook;

    private int quantityCarton;

}
