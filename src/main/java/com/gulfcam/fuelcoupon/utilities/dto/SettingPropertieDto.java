package com.gulfcam.fuelcoupon.utilities.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingPropertieDto {
    @Schema(description = "valeur probable de la clé", example = "SUBSCRIPTION,APPLICATIONTOOFFERJOB,VALIDATIONPROFILMEMBER,NOTIFICATIONTESTLANGUAGEFINISH,TECHNIQUESUPPORT,SUSCRIPTIONSUPPORT,OFFERJOBSUPPORT,EVALUATIONSUPPORT,FORMATIONSUPPORT,RECRUITERPROCESSUSSUPPORT,OTHER")
    private String key;

    private String value;
}
