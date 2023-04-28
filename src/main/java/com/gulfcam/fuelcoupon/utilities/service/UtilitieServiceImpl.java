package com.gulfcam.fuelcoupon.utilities.service;

import com.gulfcam.fuelcoupon.utilities.entity.SettingProperties;
import com.gulfcam.fuelcoupon.utilities.repository.ISettingPropertieRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class UtilitieServiceImpl implements IUtilitieService{

    @Autowired
    ISettingPropertieRepo settingPropertieRepo;

    @Override
    public SettingProperties createSetting(SettingProperties settingProperties) {
        return settingPropertieRepo.save(settingProperties);
    }

    @Override
    public SettingProperties updateSetting(SettingProperties settingProperties, Long id_setting) {
       SettingProperties setting = getOneSetting(id_setting);
       setting.setKey(settingProperties.getKey());
       setting.setValue(settingProperties.getValue());
        return settingPropertieRepo.save(setting);
    }

  SettingProperties  getOneSetting(Long id_setting) {
     return settingPropertieRepo.findById(id_setting).orElseThrow(() -> new ResourceNotFoundException("This Setting properties not exist"));
    }

    @Override
    public List<SettingProperties> listSttingProperties() {
        return settingPropertieRepo.findAll();
    }

    @Override
    public List<SettingProperties> findSettingPropByKey(String key) {
        return settingPropertieRepo.findByKey(key);
    }

    @Override
    public void deleteSettingProperties(Long settingId) {
           SettingProperties settingProperties = getOneSetting(settingId);
           settingPropertieRepo.delete(settingProperties);
    }

}
