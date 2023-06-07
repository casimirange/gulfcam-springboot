package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.order.entity.EStatusOrder;
import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.entity.StatusOrder;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreGroupDTO;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.repository.IStoreRepo;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StoreServiceImpl implements IStoreService {

    @Autowired
    IStoreRepo iStoreRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Store> getByInternalReference(Long internelReference) {
        return iStoreRepo.getStoreByInternalReference(internelReference);
    }

    @Override
    public List<ResponseStoreGroupDTO> groupNoteBootByInternalReference(Long internelReference) {
        return iStoreRepo.groupNoteBootByInternalReference(internelReference);
    }

    @Override
    public List<Store> getStoresByLocalizationLike(String localization) {
        return iStoreRepo.getStoresByLocalizationLike(localization);
    }

    @Override
    public Page<Store> getAllStores(String idStore, int page, int size, String sort, String order) {
        Specification<Store> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (idStore != null && !idStore.isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("internalReference")), Long.parseLong(idStore)));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });
        return iStoreRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Optional<Store> getStoreById(Long id) {
        return iStoreRepo.findById(id);
    }

    @Override
    public void createStore(Store store) {
        iStoreRepo.save(store);
    }

    @Override
    public void deleteStore(Store store) {
        iStoreRepo.delete(store);
    }
}
