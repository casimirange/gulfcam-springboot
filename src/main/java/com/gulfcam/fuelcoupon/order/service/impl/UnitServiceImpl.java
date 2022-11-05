package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.entity.Unit;
import com.gulfcam.fuelcoupon.order.repository.IUnitRepo;
import com.gulfcam.fuelcoupon.order.service.IUnitService;
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
public class UnitServiceImpl implements IUnitService {

    @Autowired
    IUnitRepo iUnitRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Unit> getAllUnits(int page, int size, String sort, String order) {
        return iUnitRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Unit> getUnitsByIdTypeVoucher(Long idTypeVoucher, int page, int size, String sort, String order) {
        return iUnitRepo.getUnitsByIdTypeVoucher(idTypeVoucher,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Unit> getUnitsByIdStore(Long idStore, int page, int size, String sort, String order) {
        return iUnitRepo.getUnitsByIdStore(idStore,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Unit> getUnitById(Long id) {
        return iUnitRepo.findById(id);
    }

    @Override
    public Optional<Unit> getByInternalReference(Long internelReference) {
        return iUnitRepo.getUnitByInternalReference(internelReference);
    }

    @Override
    public void createUnit(Unit unit) {
        iUnitRepo.save(unit);
    }

    @Override
    public void deleteUnit(Unit unit) {
        iUnitRepo.delete(unit);
    }
}
