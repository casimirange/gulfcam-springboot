package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Carton;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ICartonService {

    Page<Carton> getAllCartons(int page, int size, String sort, String order);
    Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order);
    Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order);
    Optional<Carton> getCartonBySerialNumber(String serialNumber);
    Optional<Carton> getCartonById(Long id);
    void createCarton(Carton carton);
    void deleteCarton(Carton carton);
    Optional<Carton> getByInternalReference(Long internelReference);
    boolean existsCartonBySerialNumber(String serialNumber);

}
