package com.gulfcam.fuelcoupon.store.repository;
import com.gulfcam.fuelcoupon.store.entity.Notebook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface INotebookRepo extends JpaRepository<Notebook, Long> {

    Page<Notebook> getNotebooksByIdStoreKeeper(Long idStoreKeeper, Pageable pageable);
    List<Notebook> getNotebooksByIdStoreKeeper(Long idStoreKeeper);
    Page<Notebook> getNotebooksByIdStoreHouse(Long idStoreHouse, Pageable pageable);
    List<Notebook> getNotebooksByIdStoreHouse(Long idStoreHouse);
    Page<Notebook> getNotebooksByIdCarton(Long idCarton, Pageable pageable);
    List<Notebook> getNotebooksByIdCarton(Long idCarton);
    Optional<Notebook> getNotebookBySerialNumber(String serialNumber);
    Optional<Notebook> getNotebookByInternalReference(Long internalReference);

    boolean existsNotebookBySerialNumber(String serialNumber);
}
