package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Notebook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface INotebookService {

    Page<Notebook> getAllNotebooks(int page, int size, String sort, String order);
    Page<Notebook> getNotebooksByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order);
    Page<Notebook> getNotebooksByIdCarton(Long idCarton, int page, int size, String sort, String order);
    Optional<Notebook> getNotebookBySerialNumber(String serialNumber);
    Optional<Notebook> getNotebookById(Long id);
    Optional<Notebook> getByInternalReference(Long internelReference);
    Map<String, Object> createNotebook(Notebook notebook);
    void createAllNotebook(List<Notebook> notebook);
    void deleteNotebook(Notebook notebook);
    boolean existsNotebookBySerialNumber(String serialNumber);

}
