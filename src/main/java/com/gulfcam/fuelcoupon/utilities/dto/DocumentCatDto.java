package com.gulfcam.fuelcoupon.utilities.dto;

import com.gulfcam.fuelcoupon.user.entity.DocumentCategorie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DocumentCatDto {
    List<DocumentCategorie> result;
}
