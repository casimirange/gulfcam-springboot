package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Coupon;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ICouponService {

    Page<Coupon> getAllCoupons(int page, int size, String sort, String order);
    Page<Coupon> getCouponsByIdStation(Long idStation, int page, int size, String sort, String order);
    Page<Coupon> getCouponsByIdClient(Long idClient, int page, int size, String sort, String order);
    Page<Coupon> getCouponsByIdNotebook(Long idNotebook, int page, int size, String sort, String order);
    Page<Coupon> getCouponsByIdTicket(Long idTicket, int page, int size, String sort, String order);
    Optional<Coupon> getCouponBySerialNumber(String serialNumber);
    Optional<Coupon> getCouponById(Long id);
    Optional<Coupon> getByInternalReference(Long internelReference);
    void createCoupon(Coupon coupon);
    void deleteCoupon(Coupon coupon);
    boolean existsCouponBySerialNumber(String serialNumber);
    boolean existsCouponByInternalReference(Long internalReference);

}
