package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.repository.IClientRepo;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponMailDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.helper.ExcelCouponHelper;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.repository.ICouponRepo;
import com.gulfcam.fuelcoupon.store.repository.ICreditNoteRepo;
import com.gulfcam.fuelcoupon.store.repository.INotebookRepo;
import com.gulfcam.fuelcoupon.store.service.*;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import com.gulfcam.fuelcoupon.utilities.repository.IStatusRepo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CouponServiceImpl implements ICouponService {
    @Autowired
    private IClientRepo iClientRepo;

    @Autowired
    ICouponRepo iCouponRepo;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IClientService iClientService;
    @Autowired
    ITypeVoucherService iTypeVoucherService;
    @Autowired
    IStationService iStationService;
    @Autowired
    INotebookRepo iNotebookRepo;
    @Autowired
    IRequestOppositionService iRequestOppositionService;

    @Autowired
    ICreditNoteRepo iCreditNoteRepo;
    @Autowired
    ITicketService iTicketService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Coupon> getByInternalReference(Long internelReference) {
        return iCouponRepo.getCouponByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseCouponDTO> getAllCoupons(int page, int size, String sort, String order) {
        Page<Coupon> couponList = iCouponRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));


        ResponseCouponDTO responseCouponDTO;
        List<ResponseCouponDTO> responseCouponDTOList = new ArrayList<>();

        for (Coupon coupon: couponList){
            responseCouponDTO = new ResponseCouponDTO();
            responseCouponDTO.setStatus(coupon.getStatus());
            responseCouponDTO.setId(coupon.getId());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setIdClient(coupon.getIdClient());
            responseCouponDTO.setIdTypeVoucher(coupon.getIdTypeVoucher());
            responseCouponDTO.setIdRequestOpposition(coupon.getIdRequestOpposition());
            responseCouponDTO.setIdCreditNote(coupon.getIdCreditNote());
            responseCouponDTO.setIdTicket(coupon.getIdTicket());
            responseCouponDTO.setIdNotebook(coupon.getIdNotebook());
            responseCouponDTO.setIdStation(coupon.getIdStation());
            responseCouponDTO.setProductionDate(coupon.getProductionDate());
            responseCouponDTO.setModulo(coupon.getModulo());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTO.setClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get());
            responseCouponDTO.setNameClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get().getCompleteName());
            responseCouponDTO.setRequestOpposition((coupon.getIdRequestOpposition() == null)? null: iRequestOppositionService.getByInternalReference(coupon.getIdRequestOpposition()).get());
            responseCouponDTO.setStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get());
            responseCouponDTO.setNameStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get().getDesignation());
            responseCouponDTO.setTicket((coupon.getIdTicket() == null)? null: iTicketService.getByInternalReference(coupon.getIdTicket()).get());
            responseCouponDTO.setNotebook((coupon.getIdNotebook() == null)? null: iNotebookRepo.getNotebookByInternalReference(coupon.getIdNotebook()).get());
            responseCouponDTO.setCreditNote((coupon.getIdCreditNote() == null)? null: iCreditNoteRepo.getCreditNoteByInternalReference(coupon.getIdCreditNote()).get());
            responseCouponDTO.setTypeVoucher((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponDTO.setAmount((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get().getAmount());
            responseCouponDTO.setInternalReference(coupon.getInternalReference());
            responseCouponDTO.setCreatedAt(coupon.getCreatedAt());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTOList.add(responseCouponDTO);

        }
        Page<ResponseCouponDTO> responseNotebookDTOPage = new PageImpl<>(responseCouponDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)),  iCouponRepo.findAll().size());
        return responseNotebookDTOPage;
    }

    @Override
    public Page<ResponseCouponDTO> filtres(String serialNumber, String statusName, String clientName, String type, String stationName, int page, int size,
                                           String sort, String order){

        Specification<Coupon> specification = ((root, query, criteriaBuilder) -> {

            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (serialNumber != null && !serialNumber.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("serialNumber")), "%" + serialNumber + "%"));
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

            if (stationName != null && !stationName.isEmpty()){
                List<Station> stations = iStationService.getStationsByDesignationContains(stationName);
                Long ref = 0L;
                if (!stations.isEmpty()){
                    for (Station station : stations) {
                        ref = !station.getInternalReference().toString().isEmpty() ? station.getInternalReference() : 0;
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idStation")), ref));
                    }
                }else {
                    predicates.add(criteriaBuilder.equal(root.get("idStation"), ref ));
                }
            }

            if (type != null && !type.isEmpty()){
                Optional<TypeVoucher> typeVoucher = iTypeVoucherService.getByInternalReference(Long.parseLong(type));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idTypeVoucher")), typeVoucher.map(TypeVoucher::getInternalReference).orElse(null)));
            }

            if (statusName != null && !statusName.isEmpty()){
                Status status = iStatusRepo.findByName(EStatus.valueOf(statusName.toUpperCase())).get();
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        Page<Coupon> couponList = iCouponRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));


        ResponseCouponDTO responseCouponDTO;
        List<ResponseCouponDTO> responseCouponDTOList = new ArrayList<>();

        for (Coupon coupon: couponList){
            responseCouponDTO = new ResponseCouponDTO();
            responseCouponDTO.setStatus(coupon.getStatus());
            responseCouponDTO.setId(coupon.getId());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setIdClient(coupon.getIdClient());
            responseCouponDTO.setIdTypeVoucher(coupon.getIdTypeVoucher());
            responseCouponDTO.setIdRequestOpposition(coupon.getIdRequestOpposition());
            responseCouponDTO.setIdCreditNote(coupon.getIdCreditNote());
            responseCouponDTO.setIdTicket(coupon.getIdTicket());
            responseCouponDTO.setIdNotebook(coupon.getIdNotebook());
            responseCouponDTO.setIdStation(coupon.getIdStation());
            responseCouponDTO.setProductionDate(coupon.getProductionDate());
            responseCouponDTO.setModulo(coupon.getModulo());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTO.setClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get());
            responseCouponDTO.setNameClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get().getCompleteName());
            responseCouponDTO.setRequestOpposition((coupon.getIdRequestOpposition() == null)? null: iRequestOppositionService.getByInternalReference(coupon.getIdRequestOpposition()).get());
            responseCouponDTO.setStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get());
            responseCouponDTO.setNameStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get().getDesignation());
            responseCouponDTO.setTicket((coupon.getIdTicket() == null)? null: iTicketService.getByInternalReference(coupon.getIdTicket()).get());
            responseCouponDTO.setNotebook((coupon.getIdNotebook() == null)? null: iNotebookRepo.getNotebookByInternalReference(coupon.getIdNotebook()).get());
            responseCouponDTO.setCreditNote((coupon.getIdCreditNote() == null)? null: iCreditNoteRepo.getCreditNoteByInternalReference(coupon.getIdCreditNote()).get());
            responseCouponDTO.setTypeVoucher((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponDTO.setAmount((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get().getAmount());
            responseCouponDTO.setInternalReference(coupon.getInternalReference());
            responseCouponDTO.setCreatedAt(coupon.getCreatedAt());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTOList.add(responseCouponDTO);

        }

        return new PageImpl<>(responseCouponDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)),  couponList.getTotalElements());
    }

    @Override
    public Page<ResponseCouponDTO> filterCoupons(String serialNumber, String statusName, int page, int size, String sort, String order) {
        Predicate<ResponseCouponDTO> bySerialNumber = serial -> serial.getSerialNumber().contains(serialNumber);
        Predicate<ResponseCouponDTO> byStatus = status -> status.getStatus().getName().toString().equals(statusName.toUpperCase());

//        List<Coupon> couponList = iCouponRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)))
        List<Coupon> couponList = iCouponRepo.findAll()
                .stream()
                .filter(!serialNumber.equals("null") ? serial -> serial.getSerialNumber().contains(serialNumber) : sn -> !sn.getSerialNumber().contains(serialNumber))
                .filter(!statusName.equals("null") ? status -> status.getStatus().getName().toString().equals(statusName.toUpperCase()) : status -> !status.getStatus().getName().equals("null"))
                .collect(Collectors.toList());


        ResponseCouponDTO responseCouponDTO;
        List<ResponseCouponDTO> responseCouponDTOList = new ArrayList<>();

        for (Coupon coupon: couponList){
            responseCouponDTO = new ResponseCouponDTO();
            responseCouponDTO.setStatus(coupon.getStatus());
            responseCouponDTO.setId(coupon.getId());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setIdClient(coupon.getIdClient());
            responseCouponDTO.setIdTypeVoucher(coupon.getIdTypeVoucher());
            responseCouponDTO.setIdRequestOpposition(coupon.getIdRequestOpposition());
            responseCouponDTO.setIdCreditNote(coupon.getIdCreditNote());
            responseCouponDTO.setIdTicket(coupon.getIdTicket());
            responseCouponDTO.setIdNotebook(coupon.getIdNotebook());
            responseCouponDTO.setIdStation(coupon.getIdStation());
            responseCouponDTO.setProductionDate(coupon.getProductionDate());
            responseCouponDTO.setModulo(coupon.getModulo());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTO.setClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get());
            responseCouponDTO.setNameClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get().getCompleteName());
            responseCouponDTO.setRequestOpposition((coupon.getIdRequestOpposition() == null)? null: iRequestOppositionService.getByInternalReference(coupon.getIdRequestOpposition()).get());
            responseCouponDTO.setStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get());
            responseCouponDTO.setNameStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get().getDesignation());
            responseCouponDTO.setTicket((coupon.getIdTicket() == null)? null: iTicketService.getByInternalReference(coupon.getIdTicket()).get());
            responseCouponDTO.setNotebook((coupon.getIdNotebook() == null)? null: iNotebookRepo.getNotebookByInternalReference(coupon.getIdNotebook()).get());
            responseCouponDTO.setCreditNote((coupon.getIdCreditNote() == null)? null: iCreditNoteRepo.getCreditNoteByInternalReference(coupon.getIdCreditNote()).get());
            responseCouponDTO.setTypeVoucher((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponDTO.setAmount((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get().getAmount());
            responseCouponDTO.setInternalReference(coupon.getInternalReference());
            responseCouponDTO.setCreatedAt(coupon.getCreatedAt());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTOList.add(responseCouponDTO);

        }

        List<ResponseCouponDTO> responseCouponDTOList2 = responseCouponDTOList
                .stream()
                .filter(!serialNumber.equals("null") ? bySerialNumber : sn -> !sn.getSerialNumber().contains(serialNumber))
                .filter(!statusName.equals("null") ? byStatus : status -> !status.getStatus().getName().equals("null"))
                .collect(Collectors.toList());
        Page<ResponseCouponDTO> responseNotebookDTOPage = new PageImpl<>(responseCouponDTOList2, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)),  responseCouponDTOList2.size());
        return responseNotebookDTOPage;
    }

    @Override
    public List<Coupon> getCouponsByIdRequestOpposition(Long idRequestOpposition) {
        return iCouponRepo.getCouponsByIdRequestOpposition(idRequestOpposition);
    }

    @Override
    public List<Coupon> getCouponsByIdCreditNote(Long idCreditNote) {
        return iCouponRepo.getCouponsByIdCreditNote(idCreditNote);
    }

    @Override
    public ByteArrayInputStream exportCouponsByIdClient(Long idClient) {

        List<Coupon> couponList = iCouponRepo.getCouponsByIdClient(idClient);
        ResponseCouponMailDTO responseCouponMailDTO;
        List<ResponseCouponMailDTO> responseCouponMailList = new ArrayList<>();
        for(Coupon item: couponList) {
            TypeVoucher typeVoucher = new TypeVoucher();

            if(item.getIdTypeVoucher() != null)
                typeVoucher = iTypeVoucherService.getByInternalReference(item.getIdTypeVoucher()).get();


            responseCouponMailDTO = new ResponseCouponMailDTO();
            responseCouponMailDTO.setInternalReference(item.getInternalReference());
            responseCouponMailDTO.setCreatedAt(item.getCreatedAt());
            responseCouponMailDTO.setModulo(item.getModulo());
            responseCouponMailDTO.setStatus(item.getStatus());
            responseCouponMailDTO.setProductionDate(item.getProductionDate());
            responseCouponMailDTO.setSerialNumber(item.getSerialNumber());
            responseCouponMailDTO.setIdTypeVoucher(typeVoucher);
            responseCouponMailList.add(responseCouponMailDTO);
        }

        return ExcelCouponHelper.couponsToExcel(responseCouponMailList);
    }

    @Override
    public Page<ResponseCouponDTO> getCouponsByIdStation(Long idStation, LocalDate period, int page, int size, String sort, String order) {
        LocalDate noDate = LocalDate.of(1900, Month.JANUARY, 1);
        Predicate<ResponseCouponDTO> byDate = date -> date.getUpdateAt().toLocalDate().isEqual(period);
        Page<Coupon> couponList = iCouponRepo.getCouponsByIdStation(idStation, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

        ResponseCouponDTO responseCouponDTO;
        List<ResponseCouponDTO> responseCouponDTOList = new ArrayList<>();

        for (Coupon coupon: couponList){
            responseCouponDTO = new ResponseCouponDTO();
            responseCouponDTO.setStatus(coupon.getStatus());
            responseCouponDTO.setId(coupon.getId());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setIdClient(coupon.getIdClient());
            responseCouponDTO.setIdTypeVoucher(coupon.getIdTypeVoucher());
            responseCouponDTO.setIdRequestOpposition(coupon.getIdRequestOpposition());
            responseCouponDTO.setIdCreditNote(coupon.getIdCreditNote());
            responseCouponDTO.setIdTicket(coupon.getIdTicket());
            responseCouponDTO.setIdNotebook(coupon.getIdNotebook());
            responseCouponDTO.setIdStation(coupon.getIdStation());
            responseCouponDTO.setProductionDate(coupon.getProductionDate());
            responseCouponDTO.setModulo(coupon.getModulo());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTO.setClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get());
            responseCouponDTO.setNameClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get().getCompleteName());
            responseCouponDTO.setRequestOpposition((coupon.getIdRequestOpposition() == null)? null: iRequestOppositionService.getByInternalReference(coupon.getIdRequestOpposition()).get());
            responseCouponDTO.setStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get());
            responseCouponDTO.setNameStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get().getDesignation());
            responseCouponDTO.setTicket((coupon.getIdTicket() == null)? null: iTicketService.getByInternalReference(coupon.getIdTicket()).get());
            responseCouponDTO.setNotebook((coupon.getIdNotebook() == null)? null: iNotebookRepo.getNotebookByInternalReference(coupon.getIdNotebook()).get());
            responseCouponDTO.setCreditNote((coupon.getIdCreditNote() == null)? null: iCreditNoteRepo.getCreditNoteByInternalReference(coupon.getIdCreditNote()).get());
            responseCouponDTO.setTypeVoucher((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponDTO.setAmount((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get().getAmount());
            responseCouponDTO.setInternalReference(coupon.getInternalReference());
            responseCouponDTO.setCreatedAt(coupon.getCreatedAt());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTOList.add(responseCouponDTO);

        }
        List<ResponseCouponDTO> responseCouponDTOList2 = responseCouponDTOList.stream()
                .filter(period != null  ? byDate : date -> !date.getCreatedAt().toLocalDate().isEqual(noDate))
                .collect(Collectors.toList());
        Page<ResponseCouponDTO> responseNotebookDTOPage = new PageImpl<>(responseCouponDTOList2, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseCouponDTOList2.size());
        return responseNotebookDTOPage;
    }

    @Override
    public Page<ResponseCouponDTO> getCouponsByIdClient(Long idClient, int page, int size, String sort, String order) {
        Page<Coupon> couponList = iCouponRepo.getCouponsByIdClient(idClient, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

        ResponseCouponDTO responseCouponDTO;
        List<ResponseCouponDTO> responseCouponDTOList = new ArrayList<>();

        for (Coupon coupon: couponList){
            responseCouponDTO = new ResponseCouponDTO();
            responseCouponDTO.setStatus(coupon.getStatus());
            responseCouponDTO.setId(coupon.getId());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setIdClient(coupon.getIdClient());
            responseCouponDTO.setIdTypeVoucher(coupon.getIdTypeVoucher());
            responseCouponDTO.setIdRequestOpposition(coupon.getIdRequestOpposition());
            responseCouponDTO.setIdCreditNote(coupon.getIdCreditNote());
            responseCouponDTO.setIdTicket(coupon.getIdTicket());
            responseCouponDTO.setIdNotebook(coupon.getIdNotebook());
            responseCouponDTO.setIdStation(coupon.getIdStation());
            responseCouponDTO.setProductionDate(coupon.getProductionDate());
            responseCouponDTO.setModulo(coupon.getModulo());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTO.setClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get());
            responseCouponDTO.setNameClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get().getCompleteName());
            responseCouponDTO.setRequestOpposition((coupon.getIdRequestOpposition() == null)? null: iRequestOppositionService.getByInternalReference(coupon.getIdRequestOpposition()).get());
            responseCouponDTO.setStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get());
            responseCouponDTO.setNameStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get().getDesignation());
            responseCouponDTO.setTicket((coupon.getIdTicket() == null)? null: iTicketService.getByInternalReference(coupon.getIdTicket()).get());
            responseCouponDTO.setNotebook((coupon.getIdNotebook() == null)? null: iNotebookRepo.getNotebookByInternalReference(coupon.getIdNotebook()).get());
            responseCouponDTO.setCreditNote((coupon.getIdCreditNote() == null)? null: iCreditNoteRepo.getCreditNoteByInternalReference(coupon.getIdCreditNote()).get());
            responseCouponDTO.setTypeVoucher((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponDTO.setAmount((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get().getAmount());
            responseCouponDTO.setInternalReference(coupon.getInternalReference());
            responseCouponDTO.setCreatedAt(coupon.getCreatedAt());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTOList.add(responseCouponDTO);

        }
        Page<ResponseCouponDTO> responseNotebookDTOPage = new PageImpl<>(responseCouponDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iCouponRepo.getCouponsByIdClient(idClient).size());
        return responseNotebookDTOPage;
    }

    @Override
    public Page<ResponseCouponDTO> getCouponsByIdNotebook(Long idNotebook, int page, int size, String sort, String order) {
        Page<Coupon> couponList = iCouponRepo.getCouponsByIdNotebook(idNotebook, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

        ResponseCouponDTO responseCouponDTO;
        List<ResponseCouponDTO> responseCouponDTOList = new ArrayList<>();

        for (Coupon coupon: couponList){
            responseCouponDTO = new ResponseCouponDTO();
            responseCouponDTO.setStatus(coupon.getStatus());
            responseCouponDTO.setId(coupon.getId());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setSerialNumber(coupon.getSerialNumber());
            responseCouponDTO.setIdClient(coupon.getIdClient());
            responseCouponDTO.setIdTypeVoucher(coupon.getIdTypeVoucher());
            responseCouponDTO.setIdRequestOpposition(coupon.getIdRequestOpposition());
            responseCouponDTO.setIdCreditNote(coupon.getIdCreditNote());
            responseCouponDTO.setIdTicket(coupon.getIdTicket());
            responseCouponDTO.setIdNotebook(coupon.getIdNotebook());
            responseCouponDTO.setIdStation(coupon.getIdStation());
            responseCouponDTO.setProductionDate(coupon.getProductionDate());
            responseCouponDTO.setModulo(coupon.getModulo());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTO.setClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get());
            responseCouponDTO.setNameClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get().getCompleteName());
            responseCouponDTO.setRequestOpposition((coupon.getIdRequestOpposition() == null)? null: iRequestOppositionService.getByInternalReference(coupon.getIdRequestOpposition()).get());
            responseCouponDTO.setStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get());
            responseCouponDTO.setNameStation((coupon.getIdStation() == null)? null: iStationService.getByInternalReference(coupon.getIdStation()).get().getDesignation());
            responseCouponDTO.setTicket((coupon.getIdTicket() == null)? null: iTicketService.getByInternalReference(coupon.getIdTicket()).get());
            responseCouponDTO.setNotebook((coupon.getIdNotebook() == null)? null: iNotebookRepo.getNotebookByInternalReference(coupon.getIdNotebook()).get());
            responseCouponDTO.setCreditNote((coupon.getIdCreditNote() == null)? null: iCreditNoteRepo.getCreditNoteByInternalReference(coupon.getIdCreditNote()).get());
            responseCouponDTO.setTypeVoucher((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get());
            responseCouponDTO.setAmount((coupon.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(coupon.getIdTypeVoucher()).get().getAmount());
            responseCouponDTO.setInternalReference(coupon.getInternalReference());
            responseCouponDTO.setCreatedAt(coupon.getCreatedAt());
            responseCouponDTO.setUpdateAt(coupon.getUpdateAt());
            responseCouponDTOList.add(responseCouponDTO);

        }
        Page<ResponseCouponDTO> responseNotebookDTOPage = new PageImpl<>(responseCouponDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iCouponRepo.getCouponsByIdNotebook(idNotebook).size());
        return responseNotebookDTOPage;
    }

    @Override
    public List<Coupon> getCouponsByIdNotebook(Long idNotebook) {
        return iCouponRepo.getCouponsByIdNotebook(idNotebook);
    }

    @Override
    public Page<Coupon> getCouponsByIdTicket(Long idTicket, int page, int size, String sort, String order) {
        return iCouponRepo.getCouponsByIdTicket(idTicket,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Coupon> getCouponById(Long id) {
        return iCouponRepo.findById(id);
    }

    @Override
    public Optional<Coupon> getCouponBySerialNumber(String serialNumber) {
        return iCouponRepo.getCouponBySerialNumber(serialNumber);
    }

    @Override
    public boolean existsCouponBySerialNumber(String serialNumber) {
        return iCouponRepo.existsCouponBySerialNumber(serialNumber);
    }

    @Override
    public boolean existsCouponByInternalReference(Long internalReference) {
        return iCouponRepo.existsCouponByInternalReference(internalReference);
    }

    @Override
    public void createCoupon(Coupon coupon) {
        iCouponRepo.save(coupon);
    }

    @Override
    public void createAllCoupon(List<Coupon> coupon) {
        iCouponRepo.saveAll(coupon);
    }

    @Override
    public void deleteCoupon(Coupon coupon) {
        iCouponRepo.delete(coupon);
    }
}
