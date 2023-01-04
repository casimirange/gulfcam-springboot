package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.dto.ResponseNotebookDTO;
import com.gulfcam.fuelcoupon.store.entity.Notebook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface INotebookService {

    Page<ResponseNotebookDTO> getAllNotebooks(int page, int size, String sort, String order);
    Page<ResponseNotebookDTO> getNotebooksByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order);
    Page<ResponseNotebookDTO> getNotebooksByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order);
    Page<ResponseNotebookDTO> getNotebooksByIdCarton(Long idCarton, int page, int size, String sort, String order);
    Optional<Notebook> getNotebookBySerialNumber(String serialNumber);
    Optional<Notebook> getNotebookById(Long id);
    Optional<Notebook> getByInternalReference(Long internelReference);
    Map<String, Object> createNotebook(Notebook notebook);
    void createAllNotebook(List<Notebook> notebook);
    void deleteNotebook(Notebook notebook);
    boolean existsNotebookBySerialNumber(String serialNumber);

}
