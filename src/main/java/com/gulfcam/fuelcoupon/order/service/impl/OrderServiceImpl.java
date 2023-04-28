package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.order.entity.*;
import com.gulfcam.fuelcoupon.order.helper.ExcelOrderHelper;
import com.gulfcam.fuelcoupon.order.repository.IOrderRepo;
import com.gulfcam.fuelcoupon.order.repository.IStatusOrderRepo;
import com.gulfcam.fuelcoupon.order.service.IOrderService;
import com.gulfcam.fuelcoupon.order.service.IPaymentMethodService;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    @Autowired
    private IStatusOrderRepo iStatusOrderRepo;

    @Override
    public Page<ResponseOrderDTO> getAllOrders(int page, int size, String sort, String order, String createdAt, String status) {

        Page<Order> orders = iOrderRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
//        orders.filter()
        ResponseOrderDTO responseOrderDTO;
        List<ResponseOrderDTO> responseOrderDTOList = new ArrayList<>();
        for(Order item: orders){
            Client client = new Client();
            Store store = new Store();
            PaymentMethod paymentMethod = new PaymentMethod();
            Users fund = new Users();
            Users commercialAttache = new Users();
            Users managerCoupon = new Users();
            Users spaceManager2 = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdCommercialAttache() != null)
                commercialAttache = iUserService.getByInternalReference(item.getIdCommercialAttache());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdSpaceManager2() != null)
                spaceManager2 = iUserService.getByInternalReference(item.getIdSpaceManager2());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setCommercialAttache(commercialAttache);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setSpaceManager2(spaceManager2);
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
            Users commercialAttache = new Users();
            Users managerCoupon = new Users();
            Users spaceManager2 = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdCommercialAttache() != null)
                commercialAttache = iUserService.getByInternalReference(item.getIdCommercialAttache());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdSpaceManager2() != null)
                spaceManager2 = iUserService.getByInternalReference(item.getIdSpaceManager2());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setCommercialAttache(commercialAttache);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setSpaceManager2(spaceManager2);
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
            Users commercialAttache = new Users();
            Users managerCoupon = new Users();
            Users spaceManager2 = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdCommercialAttache() != null)
                commercialAttache = iUserService.getByInternalReference(item.getIdCommercialAttache());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdSpaceManager2() != null)
                spaceManager2 = iUserService.getByInternalReference(item.getIdSpaceManager2());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setCommercialAttache(commercialAttache);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setSpaceManager2(spaceManager2);
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
    public Page<Order> getOrdersByIdSalesManager(Long idSalesManager, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdSalesManager(idSalesManager,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdSpaceManager1(Long idSpaceManager1, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdSpaceManager1(idSpaceManager1,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdSpaceManager2(Long idSpaceManager2, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdSpaceManager2(idSpaceManager2,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Order> getOrdersByIdCommercialAttache(Long idCommercialAttache, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdCommercialAttache(idCommercialAttache,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

//    @Override
//    public Page<Order> getOrdersByIdManagerOrder(Long idManagerOrder, int page, int size, String sort, String order) {
//        return iOrderRepo.getOrdersByIdManagerOrder(idManagerOrder,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
//    }

//    @Override
//    public Page<Order> getOrdersByIdStorekeeper(Long idStorekeeper, int page, int size, String sort, String order) {
//        return iOrderRepo.getOrdersByIdStorekeeper(idStorekeeper,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
//    }

    @Override
    public Page<Order> getOrdersByIdStore(Long idStore, int page, int size, String sort, String order) {
        return iOrderRepo.getOrdersByIdStore(idStore,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<ResponseOrderDTO> filterOrders(Long idStore, String name, LocalDate period, String internalRef, String statusName, int page, int size, String sort, String order) {
        Predicate<ResponseOrderDTO> byStore = store -> store.getStore().getInternalReference().equals(idStore);
        Predicate<ResponseOrderDTO> byClient = client -> client.getCompleteName().contains(name);
        Predicate<ResponseOrderDTO> byDate = date -> date.getCreatedAt().toLocalDate().isEqual(period);
        Predicate<ResponseOrderDTO> byRef = ref -> ref.getInternalReference().toString().contains(internalRef);
        Predicate<ResponseOrderDTO> byStatus = status -> status.getStatus().getName().toString().equals(statusName.toUpperCase());
//        iOrderRepo.findAll()
//                .stream()
//                .filter(byStore)
//                .collect(Collectors.toList());
//        return iOrderRepo.getOrdersByIdStore(idStore,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));

        List<Order> orders = iOrderRepo.findAll()
                .stream()
                .filter(idStore > 0 ? store -> store.getIdStore().equals(idStore) : store -> !store.getIdStore().equals(idStore))
                .collect(Collectors.toList()); 
//        List<Order> orders = iOrderRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)))
//                .stream()
//                .filter(idStore > 0 ? byStore : store -> !store.getIdStore().equals(idStore))
//                .filter(idStore > 0 ? byStore : store -> !store.getIdStore().equals(idStore))
//                .collect(Collectors.toList());
//        orders.filter()
        ResponseOrderDTO responseOrderDTO;
        List<ResponseOrderDTO> responseOrderDTOList = new ArrayList<>();
        for(Order item: orders){
            Client client = new Client();
            Store store = new Store();
            PaymentMethod paymentMethod = new PaymentMethod();
            Users fund = new Users();
            Users commercialAttache = new Users();
            Users managerCoupon = new Users();
            Users spaceManager2 = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdCommercialAttache() != null)
                commercialAttache = iUserService.getByInternalReference(item.getIdCommercialAttache());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdSpaceManager2() != null)
                spaceManager2 = iUserService.getByInternalReference(item.getIdSpaceManager2());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setCommercialAttache(commercialAttache);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setSpaceManager2(spaceManager2);
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

        LocalDate noDate = LocalDate.of(1900, Month.JANUARY, 1);
        List<ResponseOrderDTO> responseOrderDTOList2 = responseOrderDTOList.stream()
//                .filter(idStore > 0 ? byStore : store -> !store.getStore().getInternalReference().equals(idStore))
                .filter(!name.equals("null") ? byClient : client -> !client.getCompleteName().contains(name))
                .filter(period != null  ? byDate : date -> !date.getCreatedAt().toLocalDate().isEqual(noDate))
                .filter(!internalRef.equals("null") ? byRef : ref -> !ref.getInternalReference().toString().contains(internalRef))
                .filter(!statusName.equals("null") ? byStatus : status -> !status.getStatus().getName().equals("null"))
                .collect(Collectors.toList());

        Page<ResponseOrderDTO> orderPage = new PageImpl<>(responseOrderDTOList2, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseOrderDTOList2.size());
        return orderPage;
    }

    @Override
    public Page<ResponseOrderDTO> filtrerOrders(String idStore, String clientName, LocalDate period, String internalRef, String statusName, int page, int size, String sort, String order) {


        Specification<Order> specification = ((root, query, criteriaBuilder) -> {

            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (idStore != null && !idStore.isEmpty()){
                Store store = iStoreService.getByInternalReference(Long.parseLong(idStore)).get();
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idStore")), store.getInternalReference()));
            }

            if (clientName != null && !clientName.isEmpty()){
                List<Client> clients = iClientService.getClientsByCompleteNameContains(clientName);
                var ref = 0L;
                if (!clients.isEmpty()){
                    for (Client client : clients) {
                        ref = !client.getInternalReference().toString().isEmpty() ? client.getInternalReference() : 0;
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idClient")), ref));
                    }
                }else {
                    predicates.add(criteriaBuilder.equal(root.get("idClient"), ref ));
                }
            }

            if (period != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("createdAt").as(String.class)), period.toString() + '%'));
            }

            if (internalRef != null && !internalRef.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("internalReference").as(String.class)), '%' +internalRef + '%'));
            }

            if (statusName != null && !statusName.isEmpty()){
                StatusOrder status = iStatusOrderRepo.findByName(EStatusOrder.valueOf(statusName.toUpperCase())).get();
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        Page<Order> orders = iOrderRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

        ResponseOrderDTO responseOrderDTO;
        List<ResponseOrderDTO> responseOrderDTOList = new ArrayList<>();
        for(Order item: orders){
            Client client = new Client();
            Store store = new Store();
            PaymentMethod paymentMethod = new PaymentMethod();
            Users fund = new Users();
            Users commercialAttache = new Users();
            Users managerCoupon = new Users();
            Users spaceManager2 = new Users();

            if(item.getIdClient() != null)
                client = iClientService.getClientByInternalReference(item.getIdClient()).get();
            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if(item.getIdFund() != null)
                fund = iUserService.getByInternalReference(item.getIdFund());
            if(item.getIdCommercialAttache() != null)
                commercialAttache = iUserService.getByInternalReference(item.getIdCommercialAttache());
            if(item.getIdManagerCoupon() != null)
                managerCoupon = iUserService.getByInternalReference(item.getIdManagerCoupon());
            if(item.getIdSpaceManager2() != null)
                spaceManager2 = iUserService.getByInternalReference(item.getIdSpaceManager2());
            if(item.getIdPaymentMethod() != null)
                paymentMethod = iPaymentMethodService.getByInternalReference(item.getIdPaymentMethod()).get();

            responseOrderDTO = new ResponseOrderDTO();
            responseOrderDTO.setClient(client);
            responseOrderDTO.setStore(store);
            responseOrderDTO.setFund(fund);
            responseOrderDTO.setPaymentMethod(paymentMethod);
            responseOrderDTO.setCommercialAttache(commercialAttache);
            responseOrderDTO.setManagerCoupon(managerCoupon);
            responseOrderDTO.setSpaceManager2(spaceManager2);
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

        Page<ResponseOrderDTO> orderPage = new PageImpl<>(responseOrderDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), orders.getTotalElements());
        return orderPage;
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
