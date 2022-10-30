package com.gulfcam.fuelcoupon.utilities.service;

import com.gulfcam.fuelcoupon.user.entity.DocumentCategorie;
import com.gulfcam.fuelcoupon.utilities.entity.SettingProperties;

import java.util.List;

public interface IUtilitieService {

    SettingProperties createSetting(SettingProperties settingProperties);

    SettingProperties updateSetting(SettingProperties settingProperties, Long id_setting);

    List<SettingProperties> listSttingProperties();

    void deleteSettingProperties(Long settingId);

    List<SettingProperties> findSettingPropByKey(String key);

    List<DocumentCategorie> getAllDocumentCategorie();

}
