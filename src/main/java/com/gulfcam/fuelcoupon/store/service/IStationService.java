package com.gulfcam.fuelcoupon.store.service;

<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import com.gulfcam.fuelcoupon.store.dto.ResponseStationDTO;
import com.gulfcam.fuelcoupon.store.entity.Station;
import org.springframework.data.domain.Page;

<<<<<<< HEAD
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.Optional;

public interface IStationService {

    Page<ResponseStationDTO> getAllStations(int page, int size, String sort, String order);
    Optional<Station> getStationByPinCode(int pinCode);
    Optional<Station> getStationById(Long id);
<<<<<<< HEAD
<<<<<<< HEAD
    List<Station> getStationsByDesignationContains(String designation);
    Page<ResponseStationDTO> filtres(String designation, String localization, String pinCode, String idManagerStation, int page, int size, String sort, String order);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    void createStation(Station station);
    Optional<Station> getByInternalReference(Long internelReference);
    void deleteStation(Station station);
    boolean existsStationByPinCode(int pintCode);

}
