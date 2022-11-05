package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStokMovementRepo extends JpaRepository<StockMovement, Long> {

    Optional<StockMovement> getStockMovementByInternalReference(Long internalReference);
}
