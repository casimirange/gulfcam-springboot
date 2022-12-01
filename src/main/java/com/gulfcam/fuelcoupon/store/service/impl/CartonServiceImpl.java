package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.order.dto.ResponseUnitDTO;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.entity.Unit;
import com.gulfcam.fuelcoupon.store.dto.ResponseCartonDTO;
import com.gulfcam.fuelcoupon.store.entity.Carton;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.service.ICartonService;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CartonServiceImpl implements ICartonService {

    @Autowired
    ICartonRepo iCartonRepo;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Carton> getByInternalReference(Long internelReference) {
        return iCartonRepo.getCartonByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseCartonDTO> getAllCartons(int page, int size, String sort, String order) {
        List<Carton> cartons = iCartonRepo.findAll();
        ResponseCartonDTO responseCartonDTO;
        List<ResponseCartonDTO> responseUnitDTOList = new ArrayList<>();

        for(Carton item: cartons) {
            Users storeKeeper = new Users();
            Storehouse storehouse = new Storehouse();

            if (item.getIdStoreKeeper() != null)
                storeKeeper = iUserService.getByInternalReference(item.getIdStoreKeeper());
            if (item.getIdStoreHouse() != null)
                storehouse = iStorehouseService.getByInternalReference(item.getIdStoreHouse()).get();

            responseCartonDTO = new ResponseCartonDTO();
            responseCartonDTO.setId(item.getId());
            responseCartonDTO.setStoreHouse(storehouse);
            responseCartonDTO.setStoreKeeper(storeKeeper);
            responseCartonDTO.setStatus(item.getStatus());
            responseCartonDTO.setCreatedAt(item.getCreatedAt());
            responseCartonDTO.setInternalReference(item.getInternalReference());
            responseCartonDTO.setFrom(item.getFrom());
            responseCartonDTO.setTo(item.getTo());
            responseCartonDTO.setSerialFrom(item.getSerialFrom());
            responseCartonDTO.setNumber(item.getNumber());
            responseCartonDTO.setSerialTo(item.getSerialTo());
            responseCartonDTO.setTypeVoucher(item.getTypeVoucher());
            responseCartonDTO.setTypeVoucher(item.getTypeVoucher());
            responseCartonDTO.setUpdateAt(item.getUpdateAt());
            responseCartonDTO.setNameStoreHouse((storehouse != null)? storehouse.getName(): "");
            responseUnitDTOList.add(responseCartonDTO);
        }

        Page<ResponseCartonDTO> cartonPage = new PageImpl<>(responseUnitDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseUnitDTOList.size());
        return cartonPage;
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
    public Map<String, Object> createCarton(Carton carton) {
        iCartonRepo.save(carton);
        Map<String, Object> cartonEncoded = new HashMap<>();
        cartonEncoded.put("carton", carton);
        return cartonEncoded;
    }

    @Override
    public void deleteCarton(Carton carton) {
        iCartonRepo.delete(carton);
    }
}
