package com.gulfcam.fuelcoupon.order.service;

import com.gulfcam.fuelcoupon.order.dto.ResponseUnitDTO;
import com.gulfcam.fuelcoupon.order.entity.Unit;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IUnitService {

    Page<ResponseUnitDTO> getAllUnits(int page, int size, String sort, String order);
    Page<Unit> getUnitsByIdTypeVoucher(Long idTypeVoucher, int page, int size, String sort, String order);
    Page<Unit> getUnitsByIdStore(Long idStore, int page, int size, String sort, String order);
    Optional<Unit> getUnitById(Long id);
    Optional<Unit> getByInternalReference(Long internelReference);
    void createUnit(Unit unit);
    void deleteUnit(Unit unit);

}
