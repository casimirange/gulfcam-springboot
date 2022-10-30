package com.gulfcam.fuelcoupon.user.repository;


import com.gulfcam.fuelcoupon.user.entity.EStatusUser;
import com.gulfcam.fuelcoupon.user.entity.StatusUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "statusUsers")
public interface IStatusUserRepo extends JpaRepository<StatusUser, Long> {
	StatusUser findByName(EStatusUser name);

}
