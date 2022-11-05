package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.service.ICartonService;
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
public class CartonServiceImpl implements ICartonService {

    @Autowired
    ICartonRepo iCartonRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Carton> getByInternalReference(Long internelReference) {
        return iCartonRepo.getCartonByInternalReference(internelReference);
    }

    @Override
    public Page<Carton> getAllCartons(int page, int size, String sort, String order) {
        return iCartonRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order) {
        return iCartonRepo.getCartonsByIdStoreHouse(idStoreHouse,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order) {
        return iCartonRepo.getCartonsByIdStoreKeeper(idStoreKeeper,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Carton> getCartonById(Long id) {
        return iCartonRepo.findById(id);
    }

    @Override
    public Optional<Carton> getCartonBySerialNumber(String serialNumber) {
        return iCartonRepo.getCartonBySerialNumber(serialNumber);
    }

    @Override
    public boolean existsCartonBySerialNumber(String serialNumber) {
        return iCartonRepo.existsCartonBySerialNumber(serialNumber);
    }

    @Override
    public void createCarton(Carton carton) {
        iCartonRepo.save(carton);
    }

    @Override
    public void deleteCarton(Carton carton) {
        iCartonRepo.delete(carton);
    }
}
