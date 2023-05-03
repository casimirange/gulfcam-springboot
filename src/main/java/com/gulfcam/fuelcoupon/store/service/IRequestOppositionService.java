package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseRequestOppositionDTO;
import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import org.springframework.data.domain.Page;

<<<<<<< HEAD
<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.Map;
import java.util.Optional;

public interface IRequestOppositionService {

    Page<ResponseRequestOppositionDTO> getAllRequestOppositions(int page, int size, String sort, String order);
<<<<<<< HEAD
<<<<<<< HEAD
    Page<ResponseRequestOppositionDTO> filterRequestOppositions(String clientName, LocalDate date, String statusName, int page, int size, String sort, String order);
    Page<ResponseRequestOppositionDTO> filtres(String clientName, LocalDate date, String statusName, String idCommercialAttach, String saleManager, int page, int size, String sort, String order);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    Page<RequestOpposition> getRequestOppositionsByIdServiceClient(Long idServiceClient, int page, int size, String sort, String order);
    Page<RequestOpposition> getRequestOppositionsByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order);
    Optional<RequestOpposition> getRequestOppositionById(Long id);
    Optional<RequestOpposition> getByInternalReference(Long internelReference);
    Map<String, Object> createRequestOpposition(RequestOpposition requestOpposition);
    void deleteRequestOpposition(RequestOpposition requestOpposition);

}
