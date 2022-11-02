package com.gulfcam.fuelcoupon.order.repository;


import com.gulfcam.fuelcoupon.order.entity.EStatusOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "statusStore")
public interface IStatusOrderRepo extends JpaRepository<EStatusOrder, Long> {
	EStatusOrder findByName(EStatusOrder name);

}
