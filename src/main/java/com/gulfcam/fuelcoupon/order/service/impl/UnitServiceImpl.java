package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.dto.ResponseUnitDTO;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.entity.Unit;
import com.gulfcam.fuelcoupon.order.repository.IUnitRepo;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.order.service.IUnitService;
import com.gulfcam.fuelcoupon.store.dto.ResponseStorehouseDTO;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
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
public class UnitServiceImpl implements IUnitService {

    @Autowired
    IUnitRepo iUnitRepo;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<ResponseUnitDTO> getAllUnits(int page, int size, String sort, String order) {

        Page<Unit> units = iUnitRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseUnitDTO responseUnitDTO;
        List<ResponseUnitDTO> responseUnitDTOList = new ArrayList<>();

        for(Unit item: units) {
            Store store = new Store();
            TypeVoucher typeVoucher = new TypeVoucher();

            if (item.getIdStore() != null)
                store = iStoreService.getByInternalReference(item.getIdStore()).get();
            if (item.getIdTypeVoucher() != null)
                typeVoucher = iTypeVoucherService.getByInternalReference(item.getIdTypeVoucher()).get();

            responseUnitDTO = new ResponseUnitDTO();
            responseUnitDTO.setId(item.getId());
            responseUnitDTO.setStore(store);
            responseUnitDTO.setStatus(item.getStatus());
            responseUnitDTO.setCreatedAt(item.getCreatedAt());
            responseUnitDTO.setInternalReference(item.getInternalReference());
            responseUnitDTO.setQuantityNotebook(item.getQuantityNotebook());
            responseUnitDTO.setUpdateAt(item.getUpdateAt());
            responseUnitDTO.setAmount((typeVoucher != null)? typeVoucher.getAmount(): 0);
            responseUnitDTOList.add(responseUnitDTO);
        }

        Page<ResponseUnitDTO> unitPage = new PageImpl<>(responseUnitDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iUnitRepo.findAll().size());
        return unitPage;
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
