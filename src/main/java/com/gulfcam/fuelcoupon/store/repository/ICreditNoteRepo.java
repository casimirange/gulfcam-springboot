package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ICreditNoteRepo extends JpaRepository<CreditNote, Long> {

    Optional<CreditNote> getCreditNoteByInternalReference(Long internalReference);
}
