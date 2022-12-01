package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseCartonDTO;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

public interface ICartonService {

    Page<ResponseCartonDTO> getAllCartons(int page, int size, String sort, String order);
    Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order);
    Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order);
    Optional<Carton> getCartonById(Long id);
    Map<String, Object> createCarton(Carton carton);
    void deleteCarton(Carton carton);
    Optional<Carton> getByInternalReference(Long internelReference);

}
