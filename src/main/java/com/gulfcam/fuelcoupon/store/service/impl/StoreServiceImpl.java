package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.repository.IStoreRepo;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
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
public class StoreServiceImpl implements IStoreService {

    @Autowired
    IStoreRepo iStoreRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Store> getByInternalReference(Long internelReference) {
        return iStoreRepo.getStoreByInternalReference(internelReference);
    }

    @Override
    public Page<Store> getAllStores(int page, int size, String sort, String order) {
        return iStoreRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Optional<Store> getStoreById(Long id) {
        return iStoreRepo.findById(id);
    }

    @Override
    public void createStore(Store store) {
        iStoreRepo.save(store);
    }

    @Override
    public void deleteStore(Store store) {
        iStoreRepo.delete(store);
    }
}
