package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITypeVoucherRepo extends JpaRepository<TypeVoucher, Long> {

    Optional<TypeVoucher> getTypeVoucherByInternalReference(Long internalReference);
    Optional<TypeVoucher> getTypeVoucherByAmountEquals(float amount);
}
