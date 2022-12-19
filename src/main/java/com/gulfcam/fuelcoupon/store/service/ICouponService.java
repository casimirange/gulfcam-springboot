package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Coupon;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public interface ICouponService {

    Page<Coupon> getAllCoupons(int page, int size, String sort, String order);
    List<Coupon> getCouponsByIdRequestOpposition(Long idRequestOpposition);
    List<Coupon> getCouponsByIdCreditNote(Long idCreditNote);
    ByteArrayInputStream exportCouponsByIdClient(Long idClient);
    Page<Coupon> getCouponsByIdStation(Long idStation, int page, int size, String sort, String order);
    Page<Coupon> getCouponsByIdClient(Long idClient, int page, int size, String sort, String order);
    Page<Coupon> getCouponsByIdNotebook(Long idNotebook, int page, int size, String sort, String order);
    List<Coupon> getCouponsByIdNotebook(Long idNotebook);
    Page<Coupon> getCouponsByIdTicket(Long idTicket, int page, int size, String sort, String order);
    Optional<Coupon> getCouponBySerialNumber(String serialNumber);
    Optional<Coupon> getCouponById(Long id);
    Optional<Coupon> getByInternalReference(Long internelReference);
    void createCoupon(Coupon coupon);
    void createAllCoupon(List<Coupon> coupon);
    void deleteCoupon(Coupon coupon);
    boolean existsCouponBySerialNumber(String serialNumber);
    boolean existsCouponByInternalReference(Long internalReference);

}
