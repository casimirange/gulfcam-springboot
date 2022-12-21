package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.Notebook;
import com.gulfcam.fuelcoupon.store.repository.INotebookRepo;
import com.gulfcam.fuelcoupon.store.service.INotebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NotebookServiceImpl implements INotebookService {

    @Autowired
    INotebookRepo iNotebookRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Notebook> getByInternalReference(Long internelReference) {
        return iNotebookRepo.getNotebookByInternalReference(internelReference);
    }

    @Override
    public Page<Notebook> getAllNotebooks(int page, int size, String sort, String order) {
        return iNotebookRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Notebook> getNotebooksByIdStoreKeeper(Long idStorKeeper, int page, int size, String sort, String order) {
        return iNotebookRepo.getNotebooksByIdStoreKeeper(idStorKeeper,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Notebook> getNotebooksByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order) {
        return iNotebookRepo.getNotebooksByIdStoreHouse(idStoreHouse,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Notebook> getNotebooksByIdCarton(Long idStoreKeeper, int page, int size, String sort, String order) {
        return iNotebookRepo.getNotebooksByIdCarton(idStoreKeeper,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Notebook> getNotebookById(Long id) {
        return iNotebookRepo.findById(id);
    }

    @Override
    public Optional<Notebook> getNotebookBySerialNumber(String serialNumber) {
        return iNotebookRepo.getNotebookBySerialNumber(serialNumber);
    }

    @Override
    public boolean existsNotebookBySerialNumber(String serialNumber) {
        return iNotebookRepo.existsNotebookBySerialNumber(serialNumber);
    }

    @Override
    public Map<String, Object> createNotebook(Notebook notebook) {
        iNotebookRepo.save(notebook);
        Map<String, Object> notebookEncoded = new HashMap<>();
        notebookEncoded.put("notebook", notebook);
        return notebookEncoded;
    }

    @Override
    public void createAllNotebook(List<Notebook> notebook) {
        iNotebookRepo.saveAll(notebook);
    }

    @Override
    public void deleteNotebook(Notebook notebook) {
        iNotebookRepo.delete(notebook);
    }
}
