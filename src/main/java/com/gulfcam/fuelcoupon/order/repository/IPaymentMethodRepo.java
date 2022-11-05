package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPaymentMethodRepo extends JpaRepository<PaymentMethod, Long> {

    Optional<PaymentMethod> getPaymentMethodByInternalReference(Long internalReference);
}
