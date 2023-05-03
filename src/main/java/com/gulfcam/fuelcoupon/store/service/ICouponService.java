package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import org.springframework.data.domain.Page;
<<<<<<< HEAD
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
=======

import java.io.ByteArrayInputStream;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.List;
import java.util.Optional;

public interface ICouponService {

    Page<ResponseCouponDTO> getAllCoupons(int page, int size, String sort, String order);
<<<<<<< HEAD
    Page<ResponseCouponDTO> filtres(String serialNumber, String statusName, String clientName, String type, String stationName, int page, int size, String sort, String order);
    Page<ResponseCouponDTO> filterCoupons(String serialNumber, String statusName, int page, int size, String sort, String order);
    List<Coupon> getCouponsByIdRequestOpposition(Long idRequestOpposition);
    List<Coupon> getCouponsByIdCreditNote(Long idCreditNote);
    ByteArrayInputStream exportCouponsByIdClient(Long idClient);
    Page<ResponseCouponDTO> getCouponsByIdStation(Long idStation, LocalDate period, int page, int size, String sort, String order);
=======
    List<Coupon> getCouponsByIdRequestOpposition(Long idRequestOpposition);
    List<Coupon> getCouponsByIdCreditNote(Long idCreditNote);
    ByteArrayInputStream exportCouponsByIdClient(Long idClient);
    Page<ResponseCouponDTO> getCouponsByIdStation(Long idStation, int page, int size, String sort, String order);
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    Page<ResponseCouponDTO> getCouponsByIdClient(Long idClient, int page, int size, String sort, String order);
    Page<ResponseCouponDTO> getCouponsByIdNotebook(Long idNotebook, int page, int size, String sort, String order);
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
