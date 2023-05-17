package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.store.dto.ResponseRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import com.gulfcam.fuelcoupon.store.repository.IRequestOppositionRepo;
import com.gulfcam.fuelcoupon.store.service.IRequestOppositionService;
import com.gulfcam.fuelcoupon.store.service.ITicketService;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import com.gulfcam.fuelcoupon.utilities.repository.IStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestOppositionServiceImpl implements IRequestOppositionService {

    @Autowired
    IRequestOppositionRepo iRequestionOppositionRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Autowired
    IUserService iUserService;
    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    IClientService iClientService;

//    @Autowired
//    ITicketService iTicketService;

    @Override
    public Optional<RequestOpposition> getByInternalReference(Long internelReference) {
        return iRequestionOppositionRepo.getRequestOppositionByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseRequestOppositionDTO> getAllRequestOppositions(int page, int size, String sort, String order) {
        Page<RequestOpposition> requestOppositions = iRequestionOppositionRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        Client client;
        Users serviceClient;
        Users managerCoupon;
        ResponseRequestOppositionDTO responseRequestOppositionDTO;
        List<ResponseRequestOppositionDTO> responseRequestOppositionDTOList = new ArrayList<>();

        for (RequestOpposition requestOpposition: requestOppositions){
            client = iClientService.getClientByInternalReference(requestOpposition.getIdClient()).get();
            serviceClient = iUserService.getByInternalReference(requestOpposition.getIdCommercialAttache());
            managerCoupon = iUserService.getByInternalReference(requestOpposition.getIdSalesManager());
            responseRequestOppositionDTO = new ResponseRequestOppositionDTO();
            responseRequestOppositionDTO.setStatus(requestOpposition.getStatus());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setDescription(requestOpposition.getDescription());
            responseRequestOppositionDTO.setReason(requestOpposition.getReason());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setUpdateAt(requestOpposition.getUpdateAt());
            responseRequestOppositionDTO.setIdClient(client);
            responseRequestOppositionDTO.setNameClient(client.getCompleteName());
            responseRequestOppositionDTO.setIdCommercialAttache(serviceClient);
            responseRequestOppositionDTO.setNameCommercialAttache(serviceClient.getFirstName()+ "   " +serviceClient.getLastName());
            responseRequestOppositionDTO.setIdSalesManager(managerCoupon);
            responseRequestOppositionDTO.setNameSaleManager(managerCoupon.getFirstName()+ "   " +managerCoupon.getLastName());
            responseRequestOppositionDTO.setInternalReference(requestOpposition.getInternalReference());
            responseRequestOppositionDTO.setCreatedAt(requestOpposition.getCreatedAt());
            responseRequestOppositionDTOList.add(responseRequestOppositionDTO);

        }
        Page<ResponseRequestOppositionDTO> responseCreditNoteList = new PageImpl<>(responseRequestOppositionDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iRequestionOppositionRepo.findAll().size());
        return responseCreditNoteList;
    }

    @Override
    public Page<ResponseRequestOppositionDTO> filterRequestOppositions(String clientName, LocalDate period, String statusName, int page, int size, String sort, String order) {
        Predicate<ResponseRequestOppositionDTO> byClient = client -> client.getNameClient().contains(clientName);
        Predicate<ResponseRequestOppositionDTO> byDate = date -> date.getCreatedAt().toLocalDate().isEqual(period);
        Predicate<ResponseRequestOppositionDTO> byStatus = status -> status.getStatus().getName().toString().equals(statusName.toUpperCase());

        List<RequestOpposition> requestOppositions = iRequestionOppositionRepo.findAll();
        Client client;
        Users serviceClient;
        Users managerCoupon;
        ResponseRequestOppositionDTO responseRequestOppositionDTO;
        List<ResponseRequestOppositionDTO> responseRequestOppositionDTOList = new ArrayList<>();

        for (RequestOpposition requestOpposition: requestOppositions){
            client = iClientService.getClientByInternalReference(requestOpposition.getIdClient()).get();
            serviceClient = iUserService.getByInternalReference(requestOpposition.getIdCommercialAttache());
            managerCoupon = iUserService.getByInternalReference(requestOpposition.getIdSalesManager());
            responseRequestOppositionDTO = new ResponseRequestOppositionDTO();
            responseRequestOppositionDTO.setStatus(requestOpposition.getStatus());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setDescription(requestOpposition.getDescription());
            responseRequestOppositionDTO.setReason(requestOpposition.getReason());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setUpdateAt(requestOpposition.getUpdateAt());
            responseRequestOppositionDTO.setIdClient(client);
            responseRequestOppositionDTO.setNameClient(client.getCompleteName());
            responseRequestOppositionDTO.setIdCommercialAttache(serviceClient);
            responseRequestOppositionDTO.setNameCommercialAttache(serviceClient.getFirstName()+ "   " +serviceClient.getLastName());
            responseRequestOppositionDTO.setIdSalesManager(managerCoupon);
            responseRequestOppositionDTO.setNameSaleManager(managerCoupon.getFirstName()+ "   " +managerCoupon.getLastName());
            responseRequestOppositionDTO.setInternalReference(requestOpposition.getInternalReference());
            responseRequestOppositionDTO.setCreatedAt(requestOpposition.getCreatedAt());
            responseRequestOppositionDTOList.add(responseRequestOppositionDTO);

        }
        LocalDate noDate = LocalDate.of(1900, Month.JANUARY, 1);
        List<ResponseRequestOppositionDTO> responseRequestOppositionDTOList2 = responseRequestOppositionDTOList.stream()
                .filter(!clientName.equals("null") ? byClient : clients -> !clients.getNameClient().contains(clientName))
                .filter(period != null  ? byDate : date -> !date.getCreatedAt().toLocalDate().isEqual(noDate))
                .filter(!statusName.equals("null") ? byStatus : status -> !status.getStatus().getName().equals("null"))
                .collect(Collectors.toList());
        Page<ResponseRequestOppositionDTO> responseCreditNoteList = new PageImpl<>(responseRequestOppositionDTOList2, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseRequestOppositionDTOList2.size());
        return responseCreditNoteList;
    }

    @Override
    public Page<ResponseRequestOppositionDTO> filtres(String clientName, LocalDate date, String statusName, String idcom, String comA, int page, int size, String sort, String order) {
        Specification<RequestOpposition> specification = ((root, query, criteriaBuilder) -> {

            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (idcom != null && !idcom.isEmpty()){
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idCommercialAttache").as(String.class)), idcom));
            }

            if (comA != null && !comA.isEmpty()){
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idSalesManager").as(String.class)), comA));
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

            if (date != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("createdAt").as(String.class)), date.toString() + '%'));
            }

            if (statusName != null && !statusName.isEmpty()){
                Status status = iStatusRepo.findByName(EStatus.valueOf(statusName.toUpperCase())).get();
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        Page<RequestOpposition> requestOppositions = iRequestionOppositionRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseRequestOppositionDTO responseRequestOppositionDTO;
        List<ResponseRequestOppositionDTO> responseRequestOppositionDTOList = new ArrayList<>();
        Client client;
        Users commercialAttach = new Users();
        Users managerCoupon = new Users();

        for (RequestOpposition requestOpposition: requestOppositions){
            client = iClientService.getClientByInternalReference(requestOpposition.getIdClient()).get();
            if(requestOpposition.getIdCommercialAttache() != null)
                commercialAttach = iUserService.getByInternalReference(requestOpposition.getIdCommercialAttache());
            if(requestOpposition.getIdSalesManager() != null)
                managerCoupon = iUserService.getByInternalReference(requestOpposition.getIdSalesManager());
            responseRequestOppositionDTO = new ResponseRequestOppositionDTO();
            responseRequestOppositionDTO.setStatus(requestOpposition.getStatus());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setDescription(requestOpposition.getDescription());
            responseRequestOppositionDTO.setReason(requestOpposition.getReason());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setUpdateAt(requestOpposition.getUpdateAt());
            responseRequestOppositionDTO.setIdClient(client);
            responseRequestOppositionDTO.setNameClient(client.getCompleteName());
            responseRequestOppositionDTO.setIdCommercialAttache(commercialAttach);
            responseRequestOppositionDTO.setNameCommercialAttache(commercialAttach.getFirstName()+ "   " +commercialAttach.getLastName());
            responseRequestOppositionDTO.setIdSalesManager(managerCoupon);
            responseRequestOppositionDTO.setNameSaleManager((managerCoupon.getFirstName() == null? "" : managerCoupon.getFirstName())+ "   " +(managerCoupon.getLastName() == null ? "" : managerCoupon.getLastName()));
            responseRequestOppositionDTO.setInternalReference(requestOpposition.getInternalReference());
            responseRequestOppositionDTO.setCreatedAt(requestOpposition.getCreatedAt());
            responseRequestOppositionDTOList.add(responseRequestOppositionDTO);

        }
        return new PageImpl<>(responseRequestOppositionDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), requestOppositions.getTotalElements());
    }

    @Override
    public Page<RequestOpposition> getRequestOppositionsByIdServiceClient(Long idServiceClient, int page, int size, String sort, String order) {
//        return iRequestionOppositionRepo.getRequestOppositionsByIdServiceClient(idServiceClient,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
        return null;
    }

    @Override
    public Page<RequestOpposition> getRequestOppositionsByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order) {
//        return iRequestionOppositionRepo.getRequestOppositionsByIdManagerCoupon(idManagerCoupon,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
        return null;
    }

    @Override
    public Optional<RequestOpposition> getRequestOppositionById(Long id) {
        return iRequestionOppositionRepo.findById(id);
    }

    @Override
    public Map<String, Object> createRequestOpposition(RequestOpposition requestOpposition) {
        iRequestionOppositionRepo.save(requestOpposition);
        Map<String, Object> requestOppositionEncoded = new HashMap<>();
        requestOppositionEncoded.put("requestOpposition", requestOpposition);
        return requestOppositionEncoded;
    }

    @Override
    public void deleteRequestOpposition(RequestOpposition requestOpposition) {
        iRequestionOppositionRepo.delete(requestOpposition);
    }
}
