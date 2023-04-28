package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseStockMovementDTO;
import com.gulfcam.fuelcoupon.store.entity.StockMovement;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IStockMovementService {

    Page<ResponseStockMovementDTO> getAllStockMovements(int page, int size, String sort, String order);
    Optional<StockMovement> getStockMovementById(Long id);
    Optional<StockMovement> getByInternalReference(Long internelReference);
    void createStockMovement(StockMovement stockMovement);
    void deleteStockMovement(StockMovement stockMovement);

}
