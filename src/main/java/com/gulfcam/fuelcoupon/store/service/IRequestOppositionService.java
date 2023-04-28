package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface IRequestOppositionService {

    Page<ResponseRequestOppositionDTO> getAllRequestOppositions(int page, int size, String sort, String order);
    Page<ResponseRequestOppositionDTO> filterRequestOppositions(String clientName, LocalDate date, String statusName, int page, int size, String sort, String order);
    Page<ResponseRequestOppositionDTO> filtres(String clientName, LocalDate date, String statusName, String idCommercialAttach, String saleManager, int page, int size, String sort, String order);
    Page<RequestOpposition> getRequestOppositionsByIdServiceClient(Long idServiceClient, int page, int size, String sort, String order);
    Page<RequestOpposition> getRequestOppositionsByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order);
    Optional<RequestOpposition> getRequestOppositionById(Long id);
    Optional<RequestOpposition> getByInternalReference(Long internelReference);
    Map<String, Object> createRequestOpposition(RequestOpposition requestOpposition);
    void deleteRequestOpposition(RequestOpposition requestOpposition);

}
