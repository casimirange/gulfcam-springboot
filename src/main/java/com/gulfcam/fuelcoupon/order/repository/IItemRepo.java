package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IItemRepo extends JpaRepository<Item, Long> {

    Page<Item> getItemsByIdTypeVoucher(Long idTypeVoucher, Pageable pageable);
    Page<Item> getItemsByIdStoreHouse(Long idStoreHouse, Pageable pageable);
    Optional<Item> getItemByInternalReference(Long internalReference);
}
