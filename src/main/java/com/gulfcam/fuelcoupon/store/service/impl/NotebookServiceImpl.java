package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.dto.ResponseNotebookDTO;
import com.gulfcam.fuelcoupon.store.entity.Notebook;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.repository.INotebookRepo;
import com.gulfcam.fuelcoupon.store.service.ICartonService;
import com.gulfcam.fuelcoupon.store.service.INotebookService;
import com.gulfcam.fuelcoupon.store.service.IStorehouseService;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class NotebookServiceImpl implements INotebookService {

    @Autowired
    INotebookRepo iNotebookRepo;
    @Autowired
    ITypeVoucherService iTypeVoucherService;
    @Autowired
    ICartonRepo iCartonRepo;
    @Autowired
    IUserService iUserService;
    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Optional<Notebook> getByInternalReference(Long internelReference) {
        return iNotebookRepo.getNotebookByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseNotebookDTO> getAllNotebooks(int page, int size, String sort, String order) {
        List<Notebook> notebookList = iNotebookRepo.findAll();

        ResponseNotebookDTO responseNotebookDTO;
        List<ResponseNotebookDTO> responseNotebookDTOList = new ArrayList<>();

        for (Notebook notebook: notebookList){
            responseNotebookDTO = new ResponseNotebookDTO();
            responseNotebookDTO.setStatus(notebook.getStatus());
            responseNotebookDTO.setId(notebook.getId());
            responseNotebookDTO.setSerialNumber(notebook.getSerialNumber());
            responseNotebookDTO.setIdStoreHouse(notebook.getIdStoreHouse());
            responseNotebookDTO.setIdCarton(notebook.getIdCarton());
            responseNotebookDTO.setIdTypeVoucher(notebook.getIdTypeVoucher());
            responseNotebookDTO.setIdStoreKeeper(notebook.getIdStoreKeeper());
            responseNotebookDTO.setPlage_coupon(notebook.getPlage_coupon());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTO.setCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get());
            responseNotebookDTO.setNumberCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get().getNumber());
            responseNotebookDTO.setStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()));
            responseNotebookDTO.setNameStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getFirstName()+" "+iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getLastName());
            responseNotebookDTO.setStorehouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get());
            responseNotebookDTO.setNameStoreHouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get().getName());
            responseNotebookDTO.setTypeVoucher((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get());
            responseNotebookDTO.setAmountCoupon((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get().getAmount());
            responseNotebookDTO.setInternalReference(notebook.getInternalReference());
            responseNotebookDTO.setCreatedAt(notebook.getCreatedAt());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTOList.add(responseNotebookDTO);

        }
        Page<ResponseNotebookDTO> responseNotebookDTOPage = new PageImpl<>(responseNotebookDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseNotebookDTOList.size());
        return responseNotebookDTOPage;
    }

    @Override
    public Page<ResponseNotebookDTO> getNotebooksByIdStoreKeeper(Long idStorKeeper, int page, int size, String sort, String order) {

        List<Notebook> notebookList = iNotebookRepo.getNotebooksByIdStoreKeeper(idStorKeeper);

        ResponseNotebookDTO responseNotebookDTO;
        List<ResponseNotebookDTO> responseNotebookDTOList = new ArrayList<>();

        for (Notebook notebook: notebookList){
            responseNotebookDTO = new ResponseNotebookDTO();
            responseNotebookDTO.setStatus(notebook.getStatus());
            responseNotebookDTO.setId(notebook.getId());
            responseNotebookDTO.setSerialNumber(notebook.getSerialNumber());
            responseNotebookDTO.setIdStoreHouse(notebook.getIdStoreHouse());
            responseNotebookDTO.setIdCarton(notebook.getIdCarton());
            responseNotebookDTO.setIdTypeVoucher(notebook.getIdTypeVoucher());
            responseNotebookDTO.setIdStoreKeeper(notebook.getIdStoreKeeper());
            responseNotebookDTO.setPlage_coupon(notebook.getPlage_coupon());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTO.setCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get());
            responseNotebookDTO.setNumberCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get().getNumber());
            responseNotebookDTO.setStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()));
            responseNotebookDTO.setNameStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getFirstName()+" "+iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getLastName());
            responseNotebookDTO.setStorehouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get());
            responseNotebookDTO.setNameStoreHouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get().getName());
            responseNotebookDTO.setTypeVoucher((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get());
            responseNotebookDTO.setAmountCoupon((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get().getAmount());
            responseNotebookDTO.setInternalReference(notebook.getInternalReference());
            responseNotebookDTO.setCreatedAt(notebook.getCreatedAt());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTOList.add(responseNotebookDTO);

        }
        Page<ResponseNotebookDTO> responseNotebookDTOPage = new PageImpl<>(responseNotebookDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseNotebookDTOList.size());
        return responseNotebookDTOPage;
    }

    @Override
    public Page<ResponseNotebookDTO> getNotebooksByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order) {

        List<Notebook> notebookList = iNotebookRepo.getNotebooksByIdStoreHouse(idStoreHouse);

        ResponseNotebookDTO responseNotebookDTO;
        List<ResponseNotebookDTO> responseNotebookDTOList = new ArrayList<>();

        for (Notebook notebook: notebookList){
            responseNotebookDTO = new ResponseNotebookDTO();
            responseNotebookDTO.setStatus(notebook.getStatus());
            responseNotebookDTO.setId(notebook.getId());
            responseNotebookDTO.setSerialNumber(notebook.getSerialNumber());
            responseNotebookDTO.setIdStoreHouse(notebook.getIdStoreHouse());
            responseNotebookDTO.setIdCarton(notebook.getIdCarton());
            responseNotebookDTO.setIdTypeVoucher(notebook.getIdTypeVoucher());
            responseNotebookDTO.setIdStoreKeeper(notebook.getIdStoreKeeper());
            responseNotebookDTO.setPlage_coupon(notebook.getPlage_coupon());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTO.setCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get());
            responseNotebookDTO.setNumberCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get().getNumber());
            responseNotebookDTO.setStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()));
            responseNotebookDTO.setNameStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getFirstName()+" "+iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getLastName());
            responseNotebookDTO.setStorehouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get());
            responseNotebookDTO.setNameStoreHouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get().getName());
            responseNotebookDTO.setTypeVoucher((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get());
            responseNotebookDTO.setAmountCoupon((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get().getAmount());
            responseNotebookDTO.setInternalReference(notebook.getInternalReference());
            responseNotebookDTO.setCreatedAt(notebook.getCreatedAt());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTOList.add(responseNotebookDTO);

        }
        Page<ResponseNotebookDTO> responseNotebookDTOPage = new PageImpl<>(responseNotebookDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseNotebookDTOList.size());
        return responseNotebookDTOPage;
    }

    @Override
    public Page<ResponseNotebookDTO> getNotebooksByIdCarton(Long idCarton, int page, int size, String sort, String order) {

        List<Notebook> notebookList = iNotebookRepo.getNotebooksByIdCarton(idCarton);

        ResponseNotebookDTO responseNotebookDTO;
        List<ResponseNotebookDTO> responseNotebookDTOList = new ArrayList<>();

        for (Notebook notebook: notebookList){
            responseNotebookDTO = new ResponseNotebookDTO();
            responseNotebookDTO.setStatus(notebook.getStatus());
            responseNotebookDTO.setId(notebook.getId());
            responseNotebookDTO.setSerialNumber(notebook.getSerialNumber());
            responseNotebookDTO.setIdStoreHouse(notebook.getIdStoreHouse());
            responseNotebookDTO.setIdCarton(notebook.getIdCarton());
            responseNotebookDTO.setIdTypeVoucher(notebook.getIdTypeVoucher());
            responseNotebookDTO.setIdStoreKeeper(notebook.getIdStoreKeeper());
            responseNotebookDTO.setPlage_coupon(notebook.getPlage_coupon());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTO.setCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get());
            responseNotebookDTO.setNumberCarton((notebook.getIdCarton() == null)? null: iCartonRepo.getCartonByInternalReference(notebook.getIdCarton()).get().getNumber());
            responseNotebookDTO.setStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()));
            responseNotebookDTO.setNameStoreKeeper((notebook.getIdStoreKeeper() == null)? null: iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getFirstName()+" "+iUserService.getByInternalReference(notebook.getIdStoreKeeper()).getLastName());
            responseNotebookDTO.setStorehouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get());
            responseNotebookDTO.setNameStoreHouse((notebook.getIdStoreHouse() == null)? null: iStorehouseService.getByInternalReference(notebook.getIdStoreHouse()).get().getName());
            responseNotebookDTO.setTypeVoucher((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get());
            responseNotebookDTO.setAmountCoupon((notebook.getIdTypeVoucher() == null)? null: iTypeVoucherService.getByInternalReference(notebook.getIdTypeVoucher()).get().getAmount());
            responseNotebookDTO.setInternalReference(notebook.getInternalReference());
            responseNotebookDTO.setCreatedAt(notebook.getCreatedAt());
            responseNotebookDTO.setUpdateAt(notebook.getUpdateAt());
            responseNotebookDTOList.add(responseNotebookDTO);

        }
        Page<ResponseNotebookDTO> responseNotebookDTOPage = new PageImpl<>(responseNotebookDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseNotebookDTOList.size());
        return responseNotebookDTOPage;
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
