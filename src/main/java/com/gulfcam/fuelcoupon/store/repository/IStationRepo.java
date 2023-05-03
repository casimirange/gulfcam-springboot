package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Station;
<<<<<<< HEAD
<<<<<<< HEAD
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
=======
import org.springframework.data.jpa.repository.JpaRepository;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import org.springframework.data.jpa.repository.JpaRepository;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.Optional;

public interface IStationRepo extends JpaRepository<Station, Long> {

    Optional<Station> getStationByPinCode(int pinCode);
<<<<<<< HEAD
<<<<<<< HEAD
    List<Station> getStationsByDesignationContains(String designation);
    Optional<Station> getStationByInternalReference(Long internalReference);

    boolean existsStationByPinCode(int pinCode);

    Page<Station> findAll(Specification<Station> specification, Pageable pageable);
=======
    Optional<Station> getStationByInternalReference(Long internalReference);

    boolean existsStationByPinCode(int pinCode);
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
    Optional<Station> getStationByInternalReference(Long internalReference);

    boolean existsStationByPinCode(int pinCode);
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
}
