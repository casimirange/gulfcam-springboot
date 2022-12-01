package com.gulfcam.fuelcoupon.order.service;

import com.gulfcam.fuelcoupon.order.dto.ResponseItemDTO;
import com.gulfcam.fuelcoupon.order.entity.Item;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IItemService {

    Page<ResponseItemDTO>  getAllItems(int page, int size, String sort, String order);
    Page<Item> getItemsByIdTypeVoucher(Long idTypeVoucher, int page, int size, String sort, String order);
    Page<Item> getItemsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order);
    Optional<Item> getItemById(Long id);
    Optional<Item> getByInternalReference(Long internelReference);
    void createItem(Item item);
    void deleteItem(Item item);

}
