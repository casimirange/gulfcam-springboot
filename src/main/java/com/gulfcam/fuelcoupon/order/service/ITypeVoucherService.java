package com.gulfcam.fuelcoupon.order.service;


import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ITypeVoucherService {

    Page<TypeVoucher> getAllTypeVouchers(int page, int size, String sort, String order);
    Optional<TypeVoucher> getTypeVoucherById(Long id);
    Optional<TypeVoucher> getByInternalReference(Long internelReference);
    void createTypeVoucher(TypeVoucher typeVoucher);
    void deleteTypeVoucher(TypeVoucher typeVoucher);

}
