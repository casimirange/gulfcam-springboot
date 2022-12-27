package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Carton;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

public interface ICartonRepo extends JpaRepository<Carton, Long> {

    Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, Pageable pageable);
    List<Carton> getCartonsByIdStoreHouse(Long idStoreHouse);
    Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, Pageable pageable);
    Optional<Carton> getCartonByInternalReference(Long internalReference);
}
