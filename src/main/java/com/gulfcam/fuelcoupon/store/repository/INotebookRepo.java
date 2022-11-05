package com.gulfcam.fuelcoupon.store.repository;
import com.gulfcam.fuelcoupon.store.entity.Notebook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface INotebookRepo extends JpaRepository<Notebook, Long> {

    Page<Notebook> getNotebooksByIdStoreKeeper(Long idStoreKeeper, Pageable pageable);
    Page<Notebook> getNotebooksByIdCarton(Long idCarton, Pageable pageable);
    Optional<Notebook> getNotebookBySerialNumber(String serialNumber);
    Optional<Notebook> getNotebookByInternalReference(Long internalReference);

    boolean existsNotebookBySerialNumber(String serialNumber);
}
