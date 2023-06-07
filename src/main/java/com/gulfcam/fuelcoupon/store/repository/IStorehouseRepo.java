package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.dto.ResponseStoreGroupDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreHouseGroupDTO;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IStorehouseRepo extends JpaRepository<Storehouse, Long> {

    @Query(nativeQuery = true)
    List<ResponseStoreHouseGroupDTO> groupeNoteBookByInternalReference(@Param("reference") Long internalReference);

    Page<Storehouse> getStorehousesByIdStore(Long idStore, Pageable pageable);
    Optional<Storehouse> getStorehouseByInternalReference(Long internalReference);

    Page<Storehouse> findAll(Specification<Storehouse> specification, Pageable pageable);
}
