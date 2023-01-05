package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICreditNoteRepo extends JpaRepository<CreditNote, Long> {

    Page<CreditNote> getCreditNotesByIdStation(Long idStation, Pageable pageable);
    List<CreditNote> getCreditNotesByIdStation(Long idStation);
    Optional<CreditNote> getCreditNoteByInternalReference(Long internalReference);
}
