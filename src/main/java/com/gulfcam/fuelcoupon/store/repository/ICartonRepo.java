package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Carton;
import org.springframework.data.domain.Page;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
=======
import org.springframework.data.domain.Pageable;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import org.springframework.data.domain.Pageable;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

public interface ICartonRepo extends JpaRepository<Carton, Long> {

    Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, Pageable pageable);
    List<Carton> getCartonsByIdStoreHouse(Long idStoreHouse);
    Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, Pageable pageable);
    Page<Carton> getCartonsByIdSpaceManager1(Long idSpaceManager1, Pageable pageable);
    Optional<Carton> getCartonByInternalReference(Long internalReference);
<<<<<<< HEAD
<<<<<<< HEAD

    Page<Carton> findAll(Specification<Carton> specification, Pageable pageable);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
}
