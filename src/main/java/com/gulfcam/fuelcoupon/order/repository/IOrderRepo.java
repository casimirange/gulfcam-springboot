package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface IOrderRepo extends JpaRepository<Order, Long> {

    Page<Order> getOrdersByClientReference(String clientReference, Pageable pageable);
    Page<Order> getOrdersByIdClient(Long idClient, Pageable pageable);
    List<Order> getOrdersByIdClient(Long idClient);
    Page<Order> getOrdersByIdFund(Long idFund, Pageable pageable);
    Page<Order> getOrdersByIdManagerCoupon(Long idManagerCoupon, Pageable pageable);
    Page<Order> getOrdersByIdSalesManager(Long idSalesManager, Pageable pageable);
    Page<Order> getOrdersByIdSpaceManager1(Long idSpaceManager1, Pageable pageable);
    Page<Order> getOrdersByIdSpaceManager2(Long idSpaceManager2, Pageable pageable);
    Page<Order> getOrdersByIdCommercialAttache(Long idCommercialAttache, Pageable pageable);
//    Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, Pageable pageable);
//    Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, Pageable pageable);
    Page<Order> getOrdersByIdStore(Long idStore, Pageable pageable);
    Optional<Order> getOrderByInternalReference(Long internalReference);
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);
}
