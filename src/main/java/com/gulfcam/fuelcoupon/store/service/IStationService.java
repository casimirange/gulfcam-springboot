package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStationDTO;
import com.gulfcam.fuelcoupon.store.entity.Station;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IStationService {

    Page<ResponseStationDTO> getAllStations(int page, int size, String sort, String order);
    Optional<Station> getStationByPinCode(int pinCode);
    Optional<Station> getStationById(Long id);
    List<Station> getStationsByDesignationContains(String designation);
    Page<ResponseStationDTO> filtres(String designation, String localization, String pinCode, String idManagerStation, int page, int size, String sort, String order);
    void createStation(Station station);
    Optional<Station> getByInternalReference(Long internelReference);
    void deleteStation(Station station);
    boolean existsStationByPinCode(int pintCode);

}
