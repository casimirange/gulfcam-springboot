package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

public interface IRequestOppositionService {

    Page<RequestOpposition> getAllRequestOppositions(int page, int size, String sort, String order);
    Page<RequestOpposition> getRequestOppositionsByIdServiceClient(Long idServiceClient, int page, int size, String sort, String order);
    Page<RequestOpposition> getRequestOppositionsByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order);
    Optional<RequestOpposition> getRequestOppositionById(Long id);
    Optional<RequestOpposition> getByInternalReference(Long internelReference);
    Map<String, Object> createRequestOpposition(RequestOpposition requestOpposition);
    void deleteRequestOpposition(RequestOpposition requestOpposition);

}
