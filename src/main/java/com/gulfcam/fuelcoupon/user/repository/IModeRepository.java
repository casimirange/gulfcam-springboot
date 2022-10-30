package com.gulfcam.fuelcoupon.user.repository;

import com.gulfcam.fuelcoupon.user.entity.Mode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IModeRepository extends JpaRepository<Mode, Long> {
	
	Optional<Mode> findByModeIdAndIsDeleteNull(Long modeId);

	Optional<Mode> findByModeNameIgnoreCaseAndIsDeleteNull(String modeName);

	Page<Mode> findByIsDeleteIsNull(Pageable p);
}
