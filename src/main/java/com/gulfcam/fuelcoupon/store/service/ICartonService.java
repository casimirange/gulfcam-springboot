package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseCartonDTO;
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import org.springframework.data.domain.Page;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICartonService {

    Page<ResponseCartonDTO> getAllCartons(int page, int size, String sort, String order);
<<<<<<< HEAD
    Page<ResponseCartonDTO> filtres(String number, String statusName, String storeHouse, LocalDate date, String spaceManager1, String type, int page, int size, String sort, String order);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order);
    List<Carton> getCartonsByIdStoreHouse(Long idStoreHouse);
    Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order);
    Page<Carton> getCartonsByIdSpaceManager1(Long idSpaceManager1, int page, int size, String sort, String order);
    Optional<Carton> getCartonById(Long id);
    Map<String, Object> createCarton(Carton carton, int diffCarton);
    Map<String, Object> supplyStoreHouse(Carton carton, Storehouse storehouse);
    void deleteCarton(Carton carton);
    Optional<Carton> getByInternalReference(Long internelReference);

}
