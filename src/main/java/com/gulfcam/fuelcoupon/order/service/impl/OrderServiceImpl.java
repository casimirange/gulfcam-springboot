package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.repository.IOrderRepo;
import com.gulfcam.fuelcoupon.order.service.IOrderService;
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
public class OrderServiceImpl implements IOrderService {

    @Autowired
    IOrderRepo iOrderRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Order> getAllOrders(int page, int size, String sort, String order) {
        return iOrderRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Order> getOrdersByClientReference(String clientReference, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByClientReference(clientReference,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdClient(Long idClient, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdClient(idClient,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdFund(Long idFund, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdFund(idFund,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdManagerCoupon(idManagerCoupon,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdManagerStore(Long idManagerStore, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdManagerStore(idManagerStore,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdStorekeeper(idStorekeeper,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdStore(Long idStore, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdStore(idStore,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return iOrderRepo.findById(id);
    }

    @Override
    public Optional<Order> getByInternalReference(Long internelReference) {
        return iOrderRepo.getOrderByInternalReference(internelReference);
    }

    @Override
    public void createOrder(Order order) {
        iOrderRepo.save(order);
    }

    @Override
    public void deleteOrder(Order order) {
        iOrderRepo.delete(order);
    }
}
