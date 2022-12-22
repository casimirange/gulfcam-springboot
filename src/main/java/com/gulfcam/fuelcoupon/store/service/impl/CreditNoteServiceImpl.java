package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import com.gulfcam.fuelcoupon.store.repository.ICreditNoteRepo;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
import com.gulfcam.fuelcoupon.store.service.ICreditNoteService;
import com.gulfcam.fuelcoupon.store.service.IStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CreditNoteServiceImpl implements ICreditNoteService {

    @Autowired
    ICreditNoteRepo iCreditNoteRepo;
    @Autowired
    ICouponService iCouponService;
    @Autowired
    IStationService iStationService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<CreditNote> getByInternalReference(Long internelReference) {
        return iCreditNoteRepo.getCreditNoteByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseCreditNoteDTO> getCreditNotesByIdStation(Long idStation, int page, int size, String sort, String order) {
        List<CreditNote> creditNotes = iCreditNoteRepo.getCreditNotesByIdStation(idStation,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));

        ResponseCreditNoteDTO responseCreditNoteDTO;
        List<ResponseCreditNoteDTO> responseCreditNoteDTOList = new ArrayList<>();

        for (CreditNote creditNote: creditNotes){
            responseCreditNoteDTO = new ResponseCreditNoteDTO();
            responseCreditNoteDTO.setStatus(creditNote.getStatus());
            responseCreditNoteDTO.setId(creditNote.getId());
            responseCreditNoteDTO.setUpdateAt(creditNote.getUpdateAt());
            responseCreditNoteDTO.setStation(iStationService.getByInternalReference(creditNote.getIdStation()).get());
            responseCreditNoteDTO.setInternalReference(creditNote.getInternalReference());
            responseCreditNoteDTO.setCreatedAt(creditNote.getCreatedAt());
            responseCreditNoteDTO.setCoupon(iCouponService.getCouponsByIdCreditNote(creditNote.getInternalReference()));
            responseCreditNoteDTOList.add(responseCreditNoteDTO);

        }
        Page<ResponseCreditNoteDTO> responseCreditNotePage = new PageImpl<>(responseCreditNoteDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseCreditNoteDTOList.size());
        return responseCreditNotePage;
    }

    @Override
    public Page<ResponseCreditNoteDTO> getAllCreditNotes(int page, int size, String sort, String order) {
        List<CreditNote> creditNotes = iCreditNoteRepo.findAll();

        ResponseCreditNoteDTO responseCreditNoteDTO;
        List<ResponseCreditNoteDTO> responseCreditNoteDTOList = new ArrayList<>();

        for (CreditNote creditNote: creditNotes){
            responseCreditNoteDTO = new ResponseCreditNoteDTO();
            responseCreditNoteDTO.setStatus(creditNote.getStatus());
            responseCreditNoteDTO.setId(creditNote.getId());
            responseCreditNoteDTO.setUpdateAt(creditNote.getUpdateAt());
            responseCreditNoteDTO.setStation(iStationService.getByInternalReference(creditNote.getIdStation()).get());
            responseCreditNoteDTO.setInternalReference(creditNote.getInternalReference());
            responseCreditNoteDTO.setCreatedAt(creditNote.getCreatedAt());
            responseCreditNoteDTO.setCoupon(iCouponService.getCouponsByIdCreditNote(creditNote.getInternalReference()));
            responseCreditNoteDTOList.add(responseCreditNoteDTO);

        }
        Page<ResponseCreditNoteDTO> responseCreditNotePage = new PageImpl<>(responseCreditNoteDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseCreditNoteDTOList.size());
        return responseCreditNotePage;
    }

    @Override
    public Optional<CreditNote> getCreditNoteById(Long id) {
        return iCreditNoteRepo.findById(id);
    }

    @Override
    public void createCreditNote(CreditNote creditNote) {
        iCreditNoteRepo.save(creditNote);
    }

    @Override
    public void deleteCreditNote(CreditNote creditNote) {
        iCreditNoteRepo.delete(creditNote);
    }
}
