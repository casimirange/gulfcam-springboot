package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseStoreGroupDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreHouseGroupDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStorehouseDTO;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IStorehouseService {

    Page<ResponseStorehouseDTO> getAllStorehouses(int page, int size, String sort, String order);
    Page<Storehouse> getStorehousesByIdStore(Long idStore, int page, int size, String sort, String order);
    Optional<Storehouse> getStorehouseById(Long id);
    List<ResponseStoreHouseGroupDTO> groupeNoteBookByInternalReference(Long internelReference);
    Optional<Storehouse> getByInternalReference(Long internelReference);
    void createStorehouse(Storehouse storehouse);
    void deleteStorehouse(Storehouse storehouse);

}
