package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreGroupDTO;
import com.gulfcam.fuelcoupon.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IStoreRepo extends JpaRepository<Store, Long> {

    @Query(nativeQuery = true)
    List<ResponseStoreGroupDTO> groupNoteBootByInternalReference(@Param("reference") Long internalReference);

    List<Store> getStoresByLocalizationLike(String localization);
    Optional<Store> getStoreByInternalReference(Long internalReference);
    Page<Store> findAll(Specification<Store> specification, Pageable pageable);
}
