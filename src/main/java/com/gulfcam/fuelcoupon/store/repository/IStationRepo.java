package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStationRepo extends JpaRepository<Station, Long> {

    Optional<Station> getStationByPinCode(int pinCode);
    Optional<Station> getStationByInternalReference(Long internalReference);

    boolean existsStationByPinCode(int pinCode);
}
