package com.gulfcam.fuelcoupon.utilities.repository;

import com.gulfcam.fuelcoupon.utilities.entity.SettingProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISettingPropertieRepo extends JpaRepository<SettingProperties,Long> {
    List<SettingProperties> findByKey(String key);
}
