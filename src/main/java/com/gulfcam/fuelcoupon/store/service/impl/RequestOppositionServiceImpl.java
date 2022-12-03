package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.RequestOpposition;
import com.gulfcam.fuelcoupon.store.repository.IRequestOppositionRepo;
import com.gulfcam.fuelcoupon.store.service.IRequestOppositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class RequestOppositionServiceImpl implements IRequestOppositionService {

    @Autowired
    IRequestOppositionRepo iRequestionOppositionRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<RequestOpposition> getByInternalReference(Long internelReference) {
        return iRequestionOppositionRepo.getRequestOppositionByInternalReference(internelReference);
    }

    @Override
    public Page<RequestOpposition> getAllRequestOppositions(int page, int size, String sort, String order) {
        return iRequestionOppositionRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<RequestOpposition> getRequestOppositionsByIdServiceClient(Long idServiceClient, int page, int size, String sort, String order) {
        return iRequestionOppositionRepo.getRequestOppositionsByIdServiceClient(idServiceClient,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<RequestOpposition> getRequestOppositionsByIdManagerCoupon(Long idManagerCoupon, int page, int size, String sort, String order) {
        return iRequestionOppositionRepo.getRequestOppositionsByIdManagerCoupon(idManagerCoupon,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<RequestOpposition> getRequestOppositionById(Long id) {
        return iRequestionOppositionRepo.findById(id);
    }

    @Override
    public Map<String, Object> createRequestOpposition(RequestOpposition requestOpposition) {
        iRequestionOppositionRepo.save(requestOpposition);
        Map<String, Object> requestOppositionEncoded = new HashMap<>();
        requestOppositionEncoded.put("requestOpposition", requestOpposition);
        return requestOppositionEncoded;
    }

    @Override
    public void deleteRequestOpposition(RequestOpposition requestOpposition) {
        iRequestionOppositionRepo.delete(requestOpposition);
    }
}
