package com.gulfcam.fuelcoupon.user.service;


import com.gulfcam.fuelcoupon.user.entity.Mode;
import org.springframework.data.domain.Page;

public interface IModeService {

	Mode findById(Long id);

	<S extends Mode> S save(S entity);

	Page<Mode> findAll();

	Mode editMode(Mode mode, Mode newMode);

	boolean existsByName(String modeName, Long modeId);

	Mode deleteMode(Long modeId);
	
	
}
