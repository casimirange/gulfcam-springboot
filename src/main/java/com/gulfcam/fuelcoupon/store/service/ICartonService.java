package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseCartonDTO;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICartonService {

    Page<ResponseCartonDTO> getAllCartons(int page, int size, String sort, String order);
    Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order);
    List<Carton> getCartonsByIdStoreHouse(Long idStoreHouse);
    Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order);
    Optional<Carton> getCartonById(Long id);
    Map<String, Object> createCarton(Carton carton, int diffCarton);
    Map<String, Object> supplyStoreHouse(Carton carton, Storehouse storehouse);
    void deleteCarton(Carton carton);
    Optional<Carton> getByInternalReference(Long internelReference);

}
