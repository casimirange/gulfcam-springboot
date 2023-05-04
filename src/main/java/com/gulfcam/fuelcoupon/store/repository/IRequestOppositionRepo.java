package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRequestOppositionRepo extends JpaRepository<RequestOpposition, Long> {

//    Page<RequestOpposition> getRequestOppositionsByIdServiceClient(Long idServiceClient, Pageable pageable);
//    Page<RequestOpposition> getRequestOppositionsByIdManagerCoupon(Long idManagerCoupon, Pageable pageable);
    Optional<RequestOpposition> getRequestOppositionByInternalReference(Long internalReference);

    Page<RequestOpposition> findAll(Specification<RequestOpposition> specification, Pageable pageable);
}
