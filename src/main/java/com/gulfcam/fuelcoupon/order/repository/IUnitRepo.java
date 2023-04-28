package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUnitRepo extends JpaRepository<Unit, Long> {

    Page<Unit> getUnitsByIdTypeVoucher(Long idTypeVoucher, Pageable pageable);
    Page<Unit> getUnitsByIdStore(Long idStore, Pageable pageable);
    Optional<Unit> getUnitByInternalReference(Long internalReference);
}
