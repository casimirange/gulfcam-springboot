package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStockMovementDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.StockMovement;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.repository.IStokMovementRepo;
import com.gulfcam.fuelcoupon.store.service.IStockMovementService;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockMovementServiceImpl implements IStockMovementService {

    @Autowired
    IStokMovementRepo iStokMovementRepo;
    @Autowired
    IStoreService iStoreService;
    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<StockMovement> getByInternalReference(Long internelReference) {
        return iStokMovementRepo.getStockMovementByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseStockMovementDTO> getAllStockMovements(int page, int size, String sort, String order) {

        Page<StockMovement> stockMovementList = iStokMovementRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

        ResponseStockMovementDTO responseStockMovementDTO;
        List<ResponseStockMovementDTO> responseStockMovementDTOList = new ArrayList<>();

        for (StockMovement stockMovement: stockMovementList){

            Storehouse storehouse1 = (stockMovement.getIdStoreHouse1() == null)? null: iStorehouseService.getByInternalReference(stockMovement.getIdStoreHouse1()).get();
            Store store1 = (stockMovement.getIdStore1() == null)? null: iStoreService.getByInternalReference(stockMovement.getIdStore1()).get();
            Storehouse storehouse2 = (stockMovement.getIdStoreHouse2() == null)? null: iStorehouseService.getByInternalReference(stockMovement.getIdStoreHouse2()).get();
            Store store2 = (stockMovement.getIdStore2() == null)? null: iStoreService.getByInternalReference(stockMovement.getIdStore2()).get();
            responseStockMovementDTO = new ResponseStockMovementDTO();
            responseStockMovementDTO.setInternalReference(stockMovement.getInternalReference());
            responseStockMovementDTO.setId(stockMovement.getId());
            responseStockMovementDTO.setId(stockMovement.getId());
            responseStockMovementDTO.setType(stockMovement.getType());
            responseStockMovementDTO.setIdStore2(stockMovement.getIdStore2());
            responseStockMovementDTO.setIdStore1(stockMovement.getIdStore1());
            responseStockMovementDTO.setIdStoreHouse2(stockMovement.getIdStoreHouse2());
            responseStockMovementDTO.setIdStoreHouse1(stockMovement.getIdStoreHouse1());
            responseStockMovementDTO.setType(stockMovement.getType());
            responseStockMovementDTO.setStore1(store1);
            responseStockMovementDTO.setStoreHouse1(storehouse1);
            responseStockMovementDTO.setStore2(store2);
            responseStockMovementDTO.setStoreHouse2(storehouse2);
            responseStockMovementDTO.setNameStore1(store1 != null ? "": store1.getLocalization());
            responseStockMovementDTO.setNameStoreHouse1(storehouse1 != null ? "": storehouse1.getName());
            responseStockMovementDTO.setNameStore2(store2 != null ? "": store2.getLocalization());
            responseStockMovementDTO.setNameStoreHouse2(storehouse2 != null ? "": storehouse2.getName());
            responseStockMovementDTO.setStore2(store2);
            responseStockMovementDTO.setStoreHouse2(storehouse2);
            responseStockMovementDTO.setInternalReference(stockMovement.getInternalReference());
            responseStockMovementDTO.setCreatedAt(stockMovement.getCreatedAt());
            responseStockMovementDTO.setUpdateAt(stockMovement.getUpdateAt());
            responseStockMovementDTOList.add(responseStockMovementDTO);

        }
        Page<ResponseStockMovementDTO> responseStockMovementDTOPage = new PageImpl<>(responseStockMovementDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iStokMovementRepo.findAll().size());
        return responseStockMovementDTOPage;
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
