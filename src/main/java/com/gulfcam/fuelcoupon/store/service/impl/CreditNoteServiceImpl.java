package com.gulfcam.fuelcoupon.store.service.impl;

<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.store.dto.ResponseCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
import com.gulfcam.fuelcoupon.store.entity.Station;
=======
import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCreditNoteDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.CreditNote;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import com.gulfcam.fuelcoupon.store.repository.ICreditNoteRepo;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
import com.gulfcam.fuelcoupon.store.service.ICreditNoteService;
import com.gulfcam.fuelcoupon.store.service.IStationService;
<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import com.gulfcam.fuelcoupon.utilities.repository.IStatusRepo;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
=======
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
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
<<<<<<< HEAD
<<<<<<< HEAD
    IStatusRepo iStatusRepo;

    @Autowired
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<CreditNote> getByInternalReference(Long internelReference) {
        return iCreditNoteRepo.getCreditNoteByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseCreditNoteDTO> getCreditNotesByIdStation(Long idStation, int page, int size, String sort, String order) {
        Page<CreditNote> creditNotes = iCreditNoteRepo.getCreditNotesByIdStation(idStation,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));

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
        Page<ResponseCreditNoteDTO> responseCreditNotePage = new PageImpl<>(responseCreditNoteDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iCreditNoteRepo.getCreditNotesByIdStation(idStation).size());
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
<<<<<<< HEAD
<<<<<<< HEAD

    @Override
    public Page<ResponseCreditNoteDTO> filtres(String stationName, String statusName, String internalRef, LocalDate date, int page, int size, String sort, String order) {
        Specification<CreditNote> specification = ((root, query, criteriaBuilder) -> {

            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (internalRef != null && !internalRef.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("internalReference").as(String.class)), "%" + internalRef + "%"));
            }

            if (stationName != null && !stationName.isEmpty()){
                List<Station> stations = iStationService.getStationsByDesignationContains(stationName);
                var ref = 0L;
                if (!stations.isEmpty()){
                    for (Station station : stations) {
                        ref = !station.getInternalReference().toString().isEmpty() ? station.getInternalReference() : 0;
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idStation")), ref));
                    }
                }else {
                    predicates.add(criteriaBuilder.equal(root.get("idStation"), ref ));
                }
            }

            if (date != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("createdAt").as(String.class)), date.toString() + '%'));
            }

            if (statusName != null && !statusName.isEmpty()){
                Status status = iStatusRepo.findByName(EStatus.valueOf(statusName.toUpperCase())).get();
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        Page<CreditNote> creditNotes = iCreditNoteRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

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
        return new PageImpl<>(responseCreditNoteDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), creditNotes.getTotalElements());
    }
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
}
