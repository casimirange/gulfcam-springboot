package com.gulfcam.fuelcoupon.order.repository;


import com.gulfcam.fuelcoupon.order.entity.EStatusOrder;
import com.gulfcam.fuelcoupon.order.entity.StatusOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "statusStore")
public interface IStatusOrderRepo extends JpaRepository<StatusOrder, Long> {
	Optional<StatusOrder> findByName(EStatusOrder name);

}
