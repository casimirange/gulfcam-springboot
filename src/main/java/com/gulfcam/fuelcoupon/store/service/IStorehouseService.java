package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IStorehouseService {

    Page<Storehouse> getAllStorehouses(int page, int size, String sort, String order);
    Page<Storehouse> getStorehousesByIdStore(Long idStore, int page, int size, String sort, String order);
    Optional<Storehouse> getStorehouseById(Long id);
    Optional<Storehouse> getByInternalReference(Long internelReference);
    void createStorehouse(Storehouse storehouse);
    void deleteStorehouse(Storehouse storehouse);

}
