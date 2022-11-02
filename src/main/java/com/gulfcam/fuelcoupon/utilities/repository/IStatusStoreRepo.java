package com.gulfcam.fuelcoupon.utilities.repository;

import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "statusStore")
public interface IStatusStoreRepo extends JpaRepository<EStatus, Long> {
	EStatus findByName(EStatus name);

}
