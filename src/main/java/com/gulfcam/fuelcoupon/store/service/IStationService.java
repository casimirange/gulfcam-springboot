package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Station;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IStationService {

    Page<Station> getAllStations(int page, int size, String sort, String order);
    Optional<Station> getStationByPinCode(int pinCode);
    Optional<Station> getStationById(Long id);
    void createStation(Station station);
    Optional<Station> getByInternalReference(Long internelReference);
    void deleteStation(Station station);
    boolean existsStationByPinCode(int pintCode);

}
