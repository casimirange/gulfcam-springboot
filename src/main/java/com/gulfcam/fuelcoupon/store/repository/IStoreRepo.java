package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStoreRepo extends JpaRepository<Store, Long> {

    Optional<Store> getStoreByInternalReference(Long internalReference);
}
