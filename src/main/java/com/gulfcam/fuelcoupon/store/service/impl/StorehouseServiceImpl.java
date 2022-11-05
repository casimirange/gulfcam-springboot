package com.gulfcam.fuelcoupon.store.service.impl;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.repository.IStorehouseRepo;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
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
public class StorehouseServiceImpl implements IStorehouseService {

    @Autowired
    IStorehouseRepo iStorehouseRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;


    @Override
    public Optional<Storehouse> getByInternalReference(Long internelReference) {
        return iStorehouseRepo.getStorehouseByInternalReference(internelReference);
    }

    @Override
    public Page<Storehouse> getAllStorehouses(int page, int size, String sort, String order) {
        return iStorehouseRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Storehouse> getStorehousesByIdStore(Long idStore, int page, int size, String sort, String order) {
        return iStorehouseRepo.getStorehousesByIdStore(idStore,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Storehouse> getStorehouseById(Long id) {
        return iStorehouseRepo.findById(id);
    }

    @Override
    public void createStorehouse(Storehouse storehouse) {
        iStorehouseRepo.save(storehouse);
    }

    @Override
    public void deleteStorehouse(Storehouse storehouse) {
        iStorehouseRepo.delete(storehouse);
    }
}
