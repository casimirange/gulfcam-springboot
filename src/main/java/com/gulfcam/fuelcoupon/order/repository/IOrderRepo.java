package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IOrderRepo extends JpaRepository<Order, Long> {

    Page<Order> getOrdersByClientReference(String clientReference, Pageable pageable);
    Page<Order> getOrdersByIdClient(Long idClient, Pageable pageable);
    List<Order> getOrdersByIdClient(Long idClient);
    Page<Order> getOrdersByIdFund(Long idFund, Pageable pageable);
    Page<Order> getOrdersByIdManagerCoupon(Long idManagerCoupon, Pageable pageable);
    Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, Pageable pageable);
    Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, Pageable pageable);
    Page<Order> getOrdersByIdStore(Long idStore, Pageable pageable);
    Optional<Order> getOrderByInternalReference(Long internalReference);
}
