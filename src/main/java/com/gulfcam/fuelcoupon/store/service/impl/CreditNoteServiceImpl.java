package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import com.gulfcam.fuelcoupon.store.repository.ICreditNoteRepo;
import com.gulfcam.fuelcoupon.store.service.ICreditNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class CreditNoteServiceImpl implements ICreditNoteService {

    @Autowired
    ICreditNoteRepo iCreditNoteRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<CreditNote> getByInternalReference(Long internelReference) {
        return iCreditNoteRepo.getCreditNoteByInternalReference(internelReference);
    }

    @Override
    public Page<CreditNote> getAllCreditNotes(int page, int size, String sort, String order) {
        return iCreditNoteRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
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
