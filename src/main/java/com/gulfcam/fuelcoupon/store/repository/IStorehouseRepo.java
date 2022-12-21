package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.dto.ResponseStoreGroupDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreHouseGroupDTO;
import com.gulfcam.fuelcoupon.store.entity.Storehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IStorehouseRepo extends JpaRepository<Storehouse, Long> {

    @Query("SELECT i.idTypeVoucher as typeVoucher,SUM(i.quantityCarton) as quantityCarton, SUM(i.quantityNotebook) as quantityNoteBook, tv.amount as amount\n" +
            "FROM Item i \n" +
            "JOIN TypeVoucher tv on i.idTypeVoucher = tv.internalReference  \n" +
            "WHERE i.idStoreHouse = :reference \n" +
            "GROUP BY i.idTypeVoucher")
    List<ResponseStoreHouseGroupDTO> groupeNoteBookByInternalReference(@Param("reference") Long internalReference);

    Page<Storehouse> getStorehousesByIdStore(Long idStore, Pageable pageable);
    Optional<Storehouse> getStorehouseByInternalReference(Long internalReference);
}
