package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.entity.PaymentMethod;
import com.gulfcam.fuelcoupon.order.repository.IPaymentMethodRepo;
import com.gulfcam.fuelcoupon.order.service.IPaymentMethodService;
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
public class PaymentMethodServiceImpl implements IPaymentMethodService {

    @Autowired
    IPaymentMethodRepo iPaymentMethodRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<PaymentMethod> getAllPaymentMethods(int page, int size, String sort, String order) {
        return iPaymentMethodRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Optional<PaymentMethod> getPaymentMethodById(Long id) {
        return iPaymentMethodRepo.findById(id);
    }

    @Override
    public Optional<PaymentMethod> getByInternalReference(Long internelReference) {
        return iPaymentMethodRepo.getPaymentMethodByInternalReference(internelReference);
    }

    @Override
    public void createPaymentMethod(PaymentMethod paymentMethod) {
        iPaymentMethodRepo.save(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(PaymentMethod paymentMethod) {
        iPaymentMethodRepo.delete(paymentMethod);
    }
}
