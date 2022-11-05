package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.StockMovement;
import com.gulfcam.fuelcoupon.store.repository.IStokMovementRepo;
import com.gulfcam.fuelcoupon.store.service.IStockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StockMovementServiceImpl implements IStockMovementService {

    @Autowired
    IStokMovementRepo iStokMovementRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<StockMovement> getByInternalReference(Long internelReference) {
        return iStokMovementRepo.getStockMovementByInternalReference(internelReference);
    }

    @Override
    public Page<StockMovement> getAllStockMovements(int page, int size, String sort, String order) {
        return iStokMovementRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Optional<StockMovement> getStockMovementById(Long id) {
        return iStokMovementRepo.findById(id);
    }

    @Override
    public void createStockMovement(StockMovement stockMovement) {
        iStokMovementRepo.save(stockMovement);
    }

    @Override
    public void deleteStockMovement(StockMovement stockMovement) {
        iStokMovementRepo.delete(stockMovement);
    }
}
