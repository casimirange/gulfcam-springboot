package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.entity.PaymentMethod;
import com.gulfcam.fuelcoupon.order.helper.ExcelOrderHelper;
import com.gulfcam.fuelcoupon.order.repository.IOrderRepo;
import com.gulfcam.fuelcoupon.order.service.IOrderService;
import com.gulfcam.fuelcoupon.order.service.IPaymentMethodService;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService {

    @Autowired
    IOrderRepo iOrderRepo;

    @Autowired
    IClientService iClientService;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    IPaymentMethodService iPaymentMethodService;

    @Autowired
    IUserService iUserService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<ResponseOrderDTO> getAllOrders(int page, int size, String sort, String order, String createdAt, String status) {

        Page<Order> orders = iOrderRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        orders.filter()
        ResponseOrderDTO responseOrderDTO;
        List<ResponseOrderDTO> responseOrderDTOList = new ArrayList<>();
        for(Order item: orders){
            Client client = new Client();
            Store store = new Store();
            PaymentMethod paymentMethod = new PaymentMethod();
            Users fund = new Users();
            Users managerOrder = new Users();
            Users managerCoupon = new Users();
            Users storeKeeper = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdManagerOrder() != null)
                managerOrder = iUserService.getByInternalReference(item.getIdManagerOrder());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdStorekeeper() != null)
                storeKeeper = iUserService.getByInternalReference(item.getIdStorekeeper());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setManagerOrder(managerOrder);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setStorekeeper(storeKeeper);
            responseOrderDTO.setCompleteName((client != null) ? client.getCompleteName(): "");
            responseOrderDTO.setLocalisation((store != null) ? store.getLocalization(): "");
            responseOrderDTO.setId(item.getId());
            responseOrderDTO.setInternalReference(item.getInternalReference());
            responseOrderDTO.setClientReference(item.getClientReference());
            responseOrderDTO.setPaymentReference(item.getPaymentReference());
            responseOrderDTO.setNetAggregateAmount(item.getNetAggregateAmount());
            responseOrderDTO.setTax(item.getTax());
            responseOrderDTO.setChannel(item.getChannel());
            responseOrderDTO.setDescription(item.getDescription());
            responseOrderDTO.setTTCAggregateAmount(item.getTTCAggregateAmount());
            responseOrderDTO.setDeliveryTime(item.getDeliveryTime());
            responseOrderDTO.setLinkInvoice(item.getLinkInvoice());
            responseOrderDTO.setLinkDelivery(item.getLinkDelivery());
            responseOrderDTO.setReasonForCancellation(item.getReasonForCancellation());
            responseOrderDTO.setCreatedAt(item.getCreatedAt());
            responseOrderDTO.setUpdateAt(item.getUpdateAt());
            responseOrderDTO.setStatus(item.getStatus());
            responseOrderDTO.setReasonForCancellation(item.getReasonForCancellation());
            responseOrderDTOList.add(responseOrderDTO);
        }

        Page<ResponseOrderDTO> orderPage = new PageImpl<>(responseOrderDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iOrderRepo.findAll().size());
        return orderPage;
    }

    @Override
    public Page<Order> getOrdersByClientReference(String clientReference, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByClientReference(clientReference,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<ResponseOrderDTO> getOrdersByIdClient(Long idClient, int page, int size, String sort, String order) {

        Page<Order> orders = iOrderRepo.getOrdersByIdClient(idClient, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseOrderDTO responseOrderDTO;
        List<ResponseOrderDTO> responseOrderDTOList = new ArrayList<>();
        for(Order item: orders){
            Client client = new Client();
            Store store = new Store();
            PaymentMethod paymentMethod = new PaymentMethod();
            Users fund = new Users();
            Users managerOrder = new Users();
            Users managerCoupon = new Users();
            Users storeKeeper = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdManagerOrder() != null)
                managerOrder = iUserService.getByInternalReference(item.getIdManagerOrder());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdStorekeeper() != null)
                storeKeeper = iUserService.getByInternalReference(item.getIdStorekeeper());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setManagerOrder(managerOrder);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setStorekeeper(storeKeeper);
            responseOrderDTO.setCompleteName((client != null) ? client.getCompleteName(): "");
            responseOrderDTO.setLocalisation((store != null) ? store.getLocalization(): "");
            responseOrderDTO.setId(item.getId());
            responseOrderDTO.setInternalReference(item.getInternalReference());
            responseOrderDTO.setClientReference(item.getClientReference());
            responseOrderDTO.setPaymentReference(item.getPaymentReference());
            responseOrderDTO.setNetAggregateAmount(item.getNetAggregateAmount());
            responseOrderDTO.setTax(item.getTax());
            responseOrderDTO.setChannel(item.getChannel());
            responseOrderDTO.setDescription(item.getDescription());
            responseOrderDTO.setTTCAggregateAmount(item.getTTCAggregateAmount());
            responseOrderDTO.setDeliveryTime(item.getDeliveryTime());
            responseOrderDTO.setLinkInvoice(item.getLinkInvoice());
            responseOrderDTO.setLinkDelivery(item.getLinkDelivery());
            responseOrderDTO.setReasonForCancellation(item.getReasonForCancellation());
            responseOrderDTO.setCreatedAt(item.getCreatedAt());
            responseOrderDTO.setUpdateAt(item.getUpdateAt());
            responseOrderDTO.setStatus(item.getStatus());
            responseOrderDTO.setReasonForCancellation(item.getReasonForCancellation());
            responseOrderDTOList.add(responseOrderDTO);
        }

        Page<ResponseOrderDTO> orderPage = new PageImpl<>(responseOrderDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iOrderRepo.getOrdersByIdClient(idClient).size());
        return orderPage;
    }

    @Override
    public ByteArrayInputStream exportOrdersByIdClient(Long idClient) {
        List<Order> orders = iOrderRepo.getOrdersByIdClient(idClient);
        ResponseOrderDTO responseOrderDTO;
        List<ResponseOrderDTO> responseOrderDTOList = new ArrayList<>();
        for(Order item: orders){
            Client client = new Client();
            Store store = new Store();
            PaymentMethod paymentMethod = new PaymentMethod();
            Users fund = new Users();
            Users managerOrder = new Users();
            Users managerCoupon = new Users();
            Users storeKeeper = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdManagerOrder() != null)
                managerOrder = iUserService.getByInternalReference(item.getIdManagerOrder());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdStorekeeper() != null)
                storeKeeper = iUserService.getByInternalReference(item.getIdStorekeeper());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setManagerOrder(managerOrder);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setStorekeeper(storeKeeper);
            responseOrderDTO.setCompleteName((client != null) ? client.getCompleteName(): "");
            responseOrderDTO.setLocalisation((store != null) ? store.getLocalization(): "");
            responseOrderDTO.setId(item.getId());
            responseOrderDTO.setInternalReference(item.getInternalReference());
            responseOrderDTO.setClientReference(item.getClientReference());
            responseOrderDTO.setPaymentReference(item.getPaymentReference());
            responseOrderDTO.setNetAggregateAmount(item.getNetAggregateAmount());
            responseOrderDTO.setTax(item.getTax());
            responseOrderDTO.setChannel(item.getChannel());
            responseOrderDTO.setDescription(item.getDescription());
            responseOrderDTO.setTTCAggregateAmount(item.getTTCAggregateAmount());
            responseOrderDTO.setDeliveryTime(item.getDeliveryTime());
            responseOrderDTO.setLinkInvoice(item.getLinkInvoice());
            responseOrderDTO.setLinkDelivery(item.getLinkDelivery());
            responseOrderDTO.setReasonForCancellation(item.getReasonForCancellation());
            responseOrderDTO.setCreatedAt(item.getCreatedAt());
            responseOrderDTO.setUpdateAt(item.getUpdateAt());
            responseOrderDTO.setStatus(item.getStatus());
            responseOrderDTO.setReasonForCancellation(item.getReasonForCancellation());
            responseOrderDTOList.add(responseOrderDTO);
        }

        return ExcelOrderHelper.ordersToExcel(responseOrderDTOList);
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
    public Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdManagerOrder(idManagerOrder,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
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
