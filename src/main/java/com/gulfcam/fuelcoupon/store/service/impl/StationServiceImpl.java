package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.repository.IStationRepo;
import com.gulfcam.fuelcoupon.store.service.IStationService;
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
public class StationServiceImpl implements IStationService {

    @Autowired
    IStationRepo iStationRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Station> getByInternalReference(Long internelReference) {
        return iStationRepo.getStationByInternalReference(internelReference);
    }

    @Override
    public Page<Station> getAllStations(int page, int size, String sort, String order) {
        return iStationRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Optional<Station> getStationById(Long id) {
        return iStationRepo.findById(id);
    }

    @Override
    public Optional<Station> getStationByPinCode(int pinCode) {
        return iStationRepo.getStationByPinCode(pinCode);
    }

    @Override
    public boolean existsStationByPinCode(int pinCode) {
        return iStationRepo.existsStationByPinCode(pinCode);
    }

    @Override
    public void createStation(Station station) {
        iStationRepo.save(station);
    }

    @Override
    public void deleteStation(Station station) {
        iStationRepo.delete(station);
    }
}
