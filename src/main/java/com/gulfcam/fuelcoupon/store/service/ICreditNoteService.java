package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import org.springframework.data.domain.Page;
import java.util.Optional;

public interface ICreditNoteService {

    Page<ResponseCreditNoteDTO> getAllCreditNotes(int page, int size, String sort, String order);
    Optional<CreditNote> getCreditNoteById(Long id);
    Optional<CreditNote> getByInternalReference(Long internelReference);
    void createCreditNote(CreditNote creditNote);
    void deleteCreditNote(CreditNote creditNote);

}
