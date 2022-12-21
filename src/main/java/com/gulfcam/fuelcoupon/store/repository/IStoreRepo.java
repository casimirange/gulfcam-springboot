package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.dto.ResponseStoreGroupDTO;
import com.gulfcam.fuelcoupon.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IStoreRepo extends JpaRepository<Store, Long> {

    @Query("SELECT u.idTypeVoucher as typeVoucher, t.amount as amount, SUM(u.quantityNotebook) as quantityNoteBook \n" +
            "FROM Unit u \n" +
            "JOIN TypeVoucher t on u.idTypeVoucher = t.internalReference  \n" +
            "WHERE u.idStore = :reference \n" +
            "GROUP BY u.idTypeVoucher")
    ResponseStoreGroupDTO groupNoteBootByInternalReference(@Param("reference") Long internalReference);

    List<Store> getStoresByLocalizationLike(String localization);
    Optional<Store> getStoreByInternalReference(Long internalReference);
}
