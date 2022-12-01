package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.dto.ResponseItemDTO;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.repository.IItemRepo;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
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
public class ItemServiceImpl implements IItemService {

    @Autowired
    IItemRepo iItemRepo;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<ResponseItemDTO> getAllItems(int page, int size, String sort, String order) {
        List<Item> items = iItemRepo.findAll();
        ResponseItemDTO responseItemDTO;
        List<ResponseItemDTO> responseItemDTOList = new ArrayList<>();

        for(Item item: items) {
            Storehouse storehouse = new Storehouse();
            TypeVoucher typeVoucher = new TypeVoucher();

            if (item.getIdStoreHouse() != null)
                storehouse = iStorehouseService.getByInternalReference(item.getIdStoreHouse()).get();
            if (item.getIdTypeVoucher() != null)
                typeVoucher = iTypeVoucherService.getByInternalReference(item.getIdTypeVoucher()).get();

            responseItemDTO = new ResponseItemDTO();
            responseItemDTO.setId(item.getId());
            responseItemDTO.setStoreHouse(storehouse);
            responseItemDTO.setStatus(item.getStatus());
            responseItemDTO.setCreatedAt(item.getCreatedAt());
            responseItemDTO.setInternalReference(item.getInternalReference());
            responseItemDTO.setQuantityNotebook(item.getQuantityNotebook());
            responseItemDTO.setQuantityCarton(item.getQuantityCarton());
            responseItemDTO.setUpdateAt(item.getUpdateAt());
            responseItemDTO.setAmount((typeVoucher != null)? typeVoucher.getAmount(): 0);
            responseItemDTOList.add(responseItemDTO);
        }

        Page<ResponseItemDTO> itemPage = new PageImpl<>(responseItemDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseItemDTOList.size());
        return itemPage;
    }

    @Override
    public Page<Item> getItemsByIdTypeVoucher(Long idTypeVoucher, int page, int size, String sort, String order) {
        return iItemRepo.getItemsByIdTypeVoucher(idTypeVoucher,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Item> getItemsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order) {
        return iItemRepo.getItemsByIdStoreHouse(idStoreHouse,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return iItemRepo.findById(id);
    }

    @Override
    public Optional<Item> getByInternalReference(Long internelReference) {
        return iItemRepo.getItemByInternalReference(internelReference);
    }

    @Override
    public void createItem(Item item) {
        iItemRepo.save(item);
    }

    @Override
    public void deleteItem(Item item) {
        iItemRepo.delete(item);
    }
}
