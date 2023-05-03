package com.gulfcam.fuelcoupon.order.service;

import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.order.entity.Order;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
<<<<<<< HEAD
<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.Optional;

public interface IOrderService {

    Page<ResponseOrderDTO> getAllOrders(int page, int size, String sort, String order, String createdAt, String status);
    Page<Order> getOrdersByClientReference(String clientReference, int page, int size, String sort, String order);
    Page<ResponseOrderDTO> getOrdersByIdClient(Long idClient, int page, int size, String sort, String order);
    ByteArrayInputStream exportOrdersByIdClient(Long idClient);
    Page<Order> getOrdersByIdFund(Long idFund, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdSalesManager(Long idSalesManager, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdSpaceManager1(Long idSpaceManager1, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdSpaceManager2(Long idSpaceManager2, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdCommercialAttache(Long idCommercialAttache, int page, int size, String sort, String order);
<<<<<<< HEAD
<<<<<<< HEAD
//    Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, int page, int size, String sort, String order);
//    Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdStore(Long idStore, int page, int size, String sort, String order);
    Page<ResponseOrderDTO> filterOrders(Long idStore, String name, LocalDate date, String internalRef, String statusName, int page, int size, String sort, String order);
    Page<ResponseOrderDTO> filtrerOrders(String idStore, String clientName, LocalDate date, String internalRef, String statusName, int page, int size, String sort, String order);
=======
    Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdStore(Long idStore, int page, int size, String sort, String order);
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
    Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdStore(Long idStore, int page, int size, String sort, String order);
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    Optional<Order> getOrderById(Long id);
    Optional<Order> getByInternalReference(Long internelReference);
    void createOrder(Order order);
    void deleteOrder(Order order);

}
