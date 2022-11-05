package com.gulfcam.fuelcoupon.order.service;


import com.gulfcam.fuelcoupon.order.entity.PaymentMethod;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IPaymentMethodService {

    Page<PaymentMethod> getAllPaymentMethods(int page, int size, String sort, String order);
    Optional<PaymentMethod> getPaymentMethodById(Long id);
    Optional<PaymentMethod> getByInternalReference(Long internelReference);
    void createPaymentMethod(PaymentMethod paymentMethod);
    void deletePaymentMethod(PaymentMethod paymentMethod);

}
