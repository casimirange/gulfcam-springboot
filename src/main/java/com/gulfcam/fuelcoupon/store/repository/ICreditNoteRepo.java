package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import org.springframework.data.domain.Page;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
=======
import org.springframework.data.domain.Pageable;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import org.springframework.data.domain.Pageable;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICreditNoteRepo extends JpaRepository<CreditNote, Long> {

    Page<CreditNote> getCreditNotesByIdStation(Long idStation, Pageable pageable);
    List<CreditNote> getCreditNotesByIdStation(Long idStation);
    Optional<CreditNote> getCreditNoteByInternalReference(Long internalReference);
<<<<<<< HEAD
<<<<<<< HEAD

    Page<CreditNote> findAll(Specification<CreditNote> specification, Pageable pageable);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
}
