package com.gulfcam.fuelcoupon.store.service.impl;

<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStationDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.repository.IStationRepo;
import com.gulfcam.fuelcoupon.store.service.IStationService;
<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.user.entity.ETypeAccount;
import com.gulfcam.fuelcoupon.user.entity.TypeAccount;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.repository.ITypeAccountRepository;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
=======
import com.gulfcam.fuelcoupon.user.service.IUserService;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import com.gulfcam.fuelcoupon.user.service.IUserService;
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
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
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
<<<<<<< HEAD
<<<<<<< HEAD
    @Autowired
    ITypeAccountRepository iTypeAccountRepository;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Station> getByInternalReference(Long internelReference) {
        return iStationRepo.getStationByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseStationDTO> getAllStations(int page, int size, String sort, String order) {
        Page<Station> stationList = iStationRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

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
        Page<ResponseStationDTO> responseStationDTOPage = new PageImpl<>(responseStationDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iStationRepo.findAll().size());
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
<<<<<<< HEAD
<<<<<<< HEAD
    public List<Station> getStationsByDesignationContains(String designation) {
        return iStationRepo.getStationsByDesignationContains(designation);
    }

    @Override
    public Page<ResponseStationDTO> filtres(String designation, String localization, String pinCode, String idManagerStation, int page, int size, String sort, String order) {
        Specification<Station> specification = ((root, query, criteriaBuilder) -> {

            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (designation != null && !designation.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("designation")), "%" + designation + "%"));
            }

            if (localization != null && !localization.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("localization")), "%" + localization + "%"));
            }

            if (pinCode != null && !pinCode.isEmpty()){
                predicates.add(criteriaBuilder.like(root.get("pinCode").as(String.class), "%" + pinCode + "%"));
            }

            if (idManagerStation != null && !idManagerStation.isEmpty()){
                predicates.add(criteriaBuilder.equal(root.get("idManagerStation"), Long.parseLong(idManagerStation)));
            }

            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        Page<Station> stations = iStationRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseStationDTO responseStationDTO;
        List<ResponseStationDTO> responseStationDTOList = new ArrayList<>();

        for (Station station: stations){
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
        return new PageImpl<>(responseStationDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), stations.getTotalElements());
    }

    @Override
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    public void createStation(Station station) {
        iStationRepo.save(station);
    }

    @Override
    public void deleteStation(Station station) {
        iStationRepo.delete(station);
    }
}
