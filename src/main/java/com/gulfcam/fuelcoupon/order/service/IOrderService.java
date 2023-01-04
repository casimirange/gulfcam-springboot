package com.gulfcam.fuelcoupon.order.service;

import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.order.entity.Order;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public interface IOrderService {

    Page<ResponseOrderDTO> getAllOrders(int page, int size, String sort, String order);
    Page<Order> getOrdersByClientReference(String clientReference, int page, int size, String sort, String order);
    Page<ResponseOrderDTO> getOrdersByIdClient(Long idClient, int page, int size, String sort, String order);
    ByteArrayInputStream exportOrdersByIdClient(Long idClient);
    Page<Order> getOrdersByIdFund(Long idFund, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, int page, int size, String sort, String order);
    Page<Order> getOrdersByIdStore(Long idStore, int page, int size, String sort, String order);
    Optional<Order> getOrderById(Long id);
    Optional<Order> getByInternalReference(Long internelReference);
    void createOrder(Order order);
    void deleteOrder(Order order);

}
