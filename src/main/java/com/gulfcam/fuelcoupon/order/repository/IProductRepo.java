package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IProductRepo extends JpaRepository<Product, Long> {

    Page<Product> getProductsByIdOrder(Long idOrder, Pageable pageable);
    List<Product> getProductsByIdOrder(Long idOrder);
    Page<Product> getProductsByIdTypeVoucher(Long idTypeVoucher, Pageable pageable);
    Optional<Product> getProductByInternalReference(Long internalReference);
}
