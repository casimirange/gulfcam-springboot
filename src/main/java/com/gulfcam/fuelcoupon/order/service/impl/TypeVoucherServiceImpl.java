package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.repository.ITypeVoucherRepo;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
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
public class TypeVoucherServiceImpl implements ITypeVoucherService {

    @Autowired
    ITypeVoucherRepo iTypeVoucherRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<TypeVoucher> getAllTypeVouchers(int page, int size, String sort, String order) {
        return iTypeVoucherRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Optional<TypeVoucher> getTypeVoucherById(Long id) {
        return iTypeVoucherRepo.findById(id);
    }

    @Override
    public Optional<TypeVoucher> getByInternalReference(Long internelReference) {
        return iTypeVoucherRepo.getTypeVoucherByInternalReference(internelReference);
    }

    @Override
    public Optional<TypeVoucher> getTypeVoucherByAmountEquals(float amount) {
        return iTypeVoucherRepo.getTypeVoucherByAmountEquals(amount);
    }

    @Override
    public void createTypeVoucher(TypeVoucher paymentMethod) {
        iTypeVoucherRepo.save(paymentMethod);
    }

    @Override
    public void deleteTypeVoucher(TypeVoucher paymentMethod) {
        iTypeVoucherRepo.delete(paymentMethod);
    }
}
