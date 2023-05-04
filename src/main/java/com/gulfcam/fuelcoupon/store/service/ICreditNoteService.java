package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface ICreditNoteService {

    Page<ResponseCreditNoteDTO> getAllCreditNotes(int page, int size, String sort, String order);
    Optional<CreditNote> getCreditNoteById(Long id);
    Optional<CreditNote> getByInternalReference(Long internelReference);
    Page<ResponseCreditNoteDTO> getCreditNotesByIdStation(Long idStation, int page, int size, String sort, String order);
    void createCreditNote(CreditNote creditNote);
    void deleteCreditNote(CreditNote creditNote);

    Page<ResponseCreditNoteDTO> filtres(String station, String statusName, String ref, LocalDate date, int page, int size, String sort, String order);

}
