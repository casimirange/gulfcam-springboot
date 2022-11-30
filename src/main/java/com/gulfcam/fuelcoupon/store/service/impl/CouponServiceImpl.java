package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.repository.ICouponRepo;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CouponServiceImpl implements ICouponService {

    @Autowired
    ICouponRepo iCouponRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Coupon> getByInternalReference(Long internelReference) {
        return iCouponRepo.getCouponByInternalReference(internelReference);
    }

    @Override
    public Page<Coupon> getAllCoupons(int page, int size, String sort, String order) {
        return iCouponRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Coupon> getCouponsByIdStation(Long idStation, int page, int size, String sort, String order) {
        return iCouponRepo.getCouponsByIdStation(idStation,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Coupon> getCouponsByIdClient(Long idClient, int page, int size, String sort, String order) {
        return iCouponRepo.getCouponsByIdClient(idClient,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Coupon> getCouponsByIdNotebook(Long idNotebook, int page, int size, String sort, String order) {
        return iCouponRepo.getCouponsByIdNotebook(idNotebook,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Coupon> getCouponsByIdTicket(Long idTicket, int page, int size, String sort, String order) {
        return iCouponRepo.getCouponsByIdTicket(idTicket,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Coupon> getCouponById(Long id) {
        return iCouponRepo.findById(id);
    }

    @Override
    public Optional<Coupon> getCouponBySerialNumber(String serialNumber) {
        return iCouponRepo.getCouponBySerialNumber(serialNumber);
    }

    @Override
    public boolean existsCouponBySerialNumber(String serialNumber) {
        return iCouponRepo.existsCouponBySerialNumber(serialNumber);
    }

    @Override
    public boolean existsCouponByInternalReference(Long internalReference) {
        return iCouponRepo.existsCouponByInternalReference(internalReference);
    }

    @Override
    public void createCoupon(Coupon coupon) {
        iCouponRepo.save(coupon);
    }

    @Override
    public void deleteCoupon(Coupon coupon) {
        iCouponRepo.delete(coupon);
    }
}
