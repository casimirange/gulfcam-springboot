package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStationDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.repository.IStationRepo;
import com.gulfcam.fuelcoupon.store.service.IStationService;
import com.gulfcam.fuelcoupon.user.service.IUserService;
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
public class StationServiceImpl implements IStationService {

    @Autowired
    IStationRepo iStationRepo;
    @Autowired
    IUserService iUserService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Station> getByInternalReference(Long internelReference) {
        return iStationRepo.getStationByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseStationDTO> getAllStations(int page, int size, String sort, String order) {
        List<Station> stationList = iStationRepo.findAll();

        ResponseStationDTO responseStationDTO;
        List<ResponseStationDTO> responseStationDTOList = new ArrayList<>();

        for (Station station: stationList){
            responseStationDTO = new ResponseStationDTO();
            responseStationDTO.setStatus(station.getStatus());
            responseStationDTO.setId(station.getId());
            responseStationDTO.setIdManagerStation(station.getIdManagerStation());
            responseStationDTO.setBalance(station.getBalance());
            responseStationDTO.setDesignation(station.getDesignation());
            responseStationDTO.setLocalization(station.getLocalization());
            responseStationDTO.setPinCode(station.getPinCode());
            responseStationDTO.setUpdateAt(station.getUpdateAt());
            responseStationDTO.setManagerStation((station.getIdManagerStation() == null)? null: iUserService.getByInternalReference(station.getIdManagerStation()));
            responseStationDTO.setNameManagerStation((station.getIdManagerStation() == null)? null: iUserService.getByInternalReference(station.getIdManagerStation()).getFirstName()+ "  "+iUserService.getByInternalReference(station.getIdManagerStation()).getLastName());
            responseStationDTO.setInternalReference(station.getInternalReference());
            responseStationDTO.setCreatedAt(station.getCreatedAt());
            responseStationDTO.setUpdateAt(station.getUpdateAt());
            responseStationDTOList.add(responseStationDTO);

        }
        Page<ResponseStationDTO> responseStationDTOPage = new PageImpl<>(responseStationDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseStationDTOList.size());
        return responseStationDTOPage;
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
