package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Store;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IStoreService {

    Page<Store> getAllStores(int page, int size, String sort, String order);
    Optional<Store> getStoreById(Long id);
    Optional<Store> getByInternalReference(Long internelReference);
    List<Store> getStoresByLocalizationLike(String localization);
    void createStore(Store store);
    void deleteStore(Store store);

}
