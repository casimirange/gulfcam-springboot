package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Station;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IStationRepo extends JpaRepository<Station, Long> {

    Optional<Station> getStationByPinCode(int pinCode);
    List<Station> getStationsByDesignationContains(String designation);
    Optional<Station> getStationByInternalReference(Long internalReference);

    boolean existsStationByPinCode(int pinCode);

    Page<Station> findAll(Specification<Station> specification, Pageable pageable);
}
