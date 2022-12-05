package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICouponRepo extends JpaRepository<Coupon, Long> {

    Page<Coupon> getCouponsByIdStation(Long idStation, Pageable pageable);
    Page<Coupon> getCouponsByIdClient(Long idClient, Pageable pageable);
    Page<Coupon> getCouponsByIdNotebook(Long idNotebook, Pageable pageable);
    Page<Coupon> getCouponsByIdTicket(Long idTicket, Pageable pageable);
    List<Coupon> getCouponsByIdRequestOpposition(Long idRequestOpposition);
    List<Coupon> getCouponsByIdCreditNote(Long idCreditNote);
    Optional<Coupon> getCouponBySerialNumber(String serialNumber);
    Optional<Coupon> getCouponByInternalReference(Long internalReference);

    boolean existsCouponBySerialNumber(String serialNumber);
    boolean existsCouponByInternalReference(Long internalReference);
}
