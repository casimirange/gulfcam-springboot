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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    IClientService iClientService;

    @Autowired
    ITicketService iTicketService;

    @Override
    public Optional<RequestOpposition> getByInternalReference(Long internelReference) {
        return iRequestionOppositionRepo.getRequestOppositionByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseRequestOppositionDTO> getAllRequestOppositions(int page, int size, String sort, String order) {
        List<RequestOpposition> requestOppositions = iRequestionOppositionRepo.findAll();
        Client client;
        Users serviceClient;
        Users managerCoupon;
        ResponseRequestOppositionDTO responseRequestOppositionDTO;
        List<ResponseRequestOppositionDTO> responseRequestOppositionDTOList = new ArrayList<>();

        for (RequestOpposition requestOpposition: requestOppositions){
            client = iClientService.getClientByInternalReference(requestOpposition.getIdClient()).get();
            serviceClient = iUserService.getByInternalReference(requestOpposition.getIdServiceClient());
            managerCoupon = iUserService.getByInternalReference(requestOpposition.getIdManagerCoupon());
            responseRequestOppositionDTO = new ResponseRequestOppositionDTO();
            responseRequestOppositionDTO.setStatus(requestOpposition.getStatus());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setDescription(requestOpposition.getDescription());
            responseRequestOppositionDTO.setReason(requestOpposition.getReason());
            responseRequestOppositionDTO.setId(requestOpposition.getId());
            responseRequestOppositionDTO.setUpdateAt(requestOpposition.getUpdateAt());
            responseRequestOppositionDTO.setIdClient(client);
            responseRequestOppositionDTO.setNameClient(client.getCompleteName());
            responseRequestOppositionDTO.setIdServiceClient(serviceClient);
            responseRequestOppositionDTO.setNzmeServiceClient(serviceClient.getFirstName()+ "   " +serviceClient.getLastName());
            responseRequestOppositionDTO.setIdManagerCoupon(managerCoupon);
            responseRequestOppositionDTO.setNameManagerCoupon(managerCoupon.getFirstName()+ "   " +managerCoupon.getLastName());
            responseRequestOppositionDTO.setInternalReference(requestOpposition.getInternalReference());
            responseRequestOppositionDTO.setCreatedAt(requestOpposition.getCreatedAt());
            responseRequestOppositionDTOList.add(responseRequestOppositionDTO);

        }
        Page<ResponseRequestOppositionDTO> responseCreditNoteList = new PageImpl<>(responseRequestOppositionDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseRequestOppositionDTOList.size());
        return responseCreditNoteList;
    }

    @Override
    public Page<RequestOpposition> getRequestOppositionsByIdServiceClient(Long idServiceClient, int page, int size, String sort, String order) {
        return iRequestionOppositionRepo.getRequestOppositionsByIdServiceClient(idServiceClient,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<RequestOpposition> getRequestOppositionsByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order) {
        return iRequestionOppositionRepo.getRequestOppositionsByIdManagerCoupon(idManagerCoupon,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
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
