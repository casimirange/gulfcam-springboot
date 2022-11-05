package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.repository.IItemRepo;
import com.gulfcam.fuelcoupon.order.service.IItemService;
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
public class ItemServiceImpl implements IItemService {

    @Autowired
    IItemRepo iItemRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Item> getAllItems(int page, int size, String sort, String order) {
        return iItemRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Item> getItemsByIdTypeVoucher(Long idTypeVoucher, int page, int size, String sort, String order) {
        return iItemRepo.getItemsByIdTypeVoucher(idTypeVoucher,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Item> getItemsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order) {
        return iItemRepo.getItemsByIdStoreHouse(idStoreHouse,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return iItemRepo.findById(id);
    }

    @Override
    public Optional<Item> getByInternalReference(Long internelReference) {
        return iItemRepo.getItemByInternalReference(internelReference);
    }

    @Override
    public void createItem(Item item) {
        iItemRepo.save(item);
    }

    @Override
    public void deleteItem(Item item) {
        iItemRepo.delete(item);
    }
}
