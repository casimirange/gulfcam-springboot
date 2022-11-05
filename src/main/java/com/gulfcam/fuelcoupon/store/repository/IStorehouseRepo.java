package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStorehouseRepo extends JpaRepository<Storehouse, Long> {

    Page<Storehouse> getStorehousesByIdStore(Long idStore, Pageable pageable);
    Optional<Storehouse> getStorehouseByInternalReference(Long internalReference);
}
