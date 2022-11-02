package com.gulfcam.fuelcoupon.utilities.repository;

import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "utilities")
public interface IStatusRepo extends JpaRepository<Status, Long> {
	Status findByName(EStatus name);

}
