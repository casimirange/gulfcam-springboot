package com.gulfcam.fuelcoupon.store.service.impl;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreHouseGroupDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStorehouseDTO;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.repository.IStorehouseRepo;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
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
public class StorehouseServiceImpl implements IStorehouseService {

    @Autowired
    IStorehouseRepo iStorehouseRepo;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    ResourceBundleMessageSource messageSource;


    @Override
    public Optional<Storehouse> getByInternalReference(Long internelReference) {
        return iStorehouseRepo.getStorehouseByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseStorehouseDTO> getAllStorehouses(int page, int size, String sort, String order) {

        List<Storehouse> storehouses = iStorehouseRepo.findAll();
        ResponseStorehouseDTO responseStorehouseDTO;
        List<ResponseStorehouseDTO> responseStorehouseDTOList = new ArrayList<>();

        for(Storehouse item: storehouses){
            Store store = new Store();

            if(item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();

            responseStorehouseDTO = new ResponseStorehouseDTO();
            responseStorehouseDTO.setId(item.getId());
            responseStorehouseDTO.setStore(store);
            responseStorehouseDTO.setStatus(item.getStatus());
            responseStorehouseDTO.setCreateAt(item.getCreateAt());
            responseStorehouseDTO.setInternalReference(item.getInternalReference());
            responseStorehouseDTO.setName(item.getName());
            responseStorehouseDTO.setType(item.getType());
            responseStorehouseDTO.setUpdateAt(item.getUpdateAt());
            responseStorehouseDTO.setLocalisationStore((store != null)? store.getLocalization(): "");
            responseStorehouseDTOList.add(responseStorehouseDTO);
        }
        Page<ResponseStorehouseDTO> storehousePage = new PageImpl<>(responseStorehouseDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseStorehouseDTOList.size());
        return storehousePage;
    }

    @Override
    public Page<Storehouse> getStorehousesByIdStore(Long idStore, int page, int size, String sort, String order) {
        return iStorehouseRepo.getStorehousesByIdStore(idStore,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Storehouse> getStorehouseById(Long id) {
        return iStorehouseRepo.findById(id);
    }

    @Override
    public List<ResponseStoreHouseGroupDTO> groupeNoteBookByInternalReference(Long internelReference) {
        return iStorehouseRepo.groupeNoteBookByInternalReference(internelReference);
    }

    @Override
    public void createStorehouse(Storehouse storehouse) {
        iStorehouseRepo.save(storehouse);
    }

    @Override
    public void deleteStorehouse(Storehouse storehouse) {
        iStorehouseRepo.delete(storehouse);
    }
}
