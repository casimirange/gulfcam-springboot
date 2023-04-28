package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.dto.ResponseProductDTO;
import com.gulfcam.fuelcoupon.order.dto.ResponseUnitDTO;
import com.gulfcam.fuelcoupon.order.entity.Order;
import com.gulfcam.fuelcoupon.order.entity.Product;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.entity.Unit;
import com.gulfcam.fuelcoupon.order.repository.IProductRepo;
import com.gulfcam.fuelcoupon.order.service.IOrderService;
import com.gulfcam.fuelcoupon.order.service.IProductService;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.entity.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    @Autowired
    IProductRepo iProductRepo;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IOrderService iOrderService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<ResponseProductDTO> getAllProducts(int page, int size, String sort, String order) {
        Page<Product> products = iProductRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseProductDTO responseProductDTO;
        List<ResponseProductDTO> responseProductDTOList = new ArrayList<>();

        for(Product item: products) {
            Order order1 = new Order();
            TypeVoucher typeVoucher = new TypeVoucher();

            if (item.getIdOrder() != null)
                order1 = iOrderService.getByInternalReference(item.getIdOrder()).get();
            if (item.getIdTypeVoucher() != null)
                typeVoucher = iTypeVoucherService.getByInternalReference(item.getIdTypeVoucher()).get();

            responseProductDTO = new ResponseProductDTO();
            responseProductDTO.setId(item.getId());
            responseProductDTO.setOrder(order1);
            responseProductDTO.setStatus(item.getStatus());
            responseProductDTO.setCreatedAt(item.getCreatedAt());
            responseProductDTO.setInternalReference(item.getInternalReference());
            responseProductDTO.setQuantityNotebook(item.getQuantityNotebook());
            responseProductDTO.setUpdateAt(item.getUpdateAt());
            responseProductDTO.setAmount((typeVoucher != null)? typeVoucher.getAmount(): 0);
            responseProductDTOList.add(responseProductDTO);
        }

        Page<ResponseProductDTO> productPage = new PageImpl<>(responseProductDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iProductRepo.findAll().size());
        return productPage;
    }

    @Override
    public Page<ResponseProductDTO> getProductsByIdOrder(Long idOrder, int page, int size, String sort, String order) {

        Page<Product> products = iProductRepo.getProductsByIdOrder(idOrder, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseProductDTO responseProductDTO;
        List<ResponseProductDTO> responseProductDTOList = new ArrayList<>();

        for(Product item: products) {
            Order order1 = new Order();
            TypeVoucher typeVoucher = new TypeVoucher();

            if (item.getIdOrder() != null)
                order1 = iOrderService.getByInternalReference(item.getIdOrder()).get();
            if (item.getIdTypeVoucher() != null)
                typeVoucher = iTypeVoucherService.getByInternalReference(item.getIdTypeVoucher()).get();

            responseProductDTO = new ResponseProductDTO();
            responseProductDTO.setId(item.getId());
            responseProductDTO.setOrder(order1);
            responseProductDTO.setStatus(item.getStatus());
            responseProductDTO.setCreatedAt(item.getCreatedAt());
            responseProductDTO.setInternalReference(item.getInternalReference());
            responseProductDTO.setQuantityNotebook(item.getQuantityNotebook());
            responseProductDTO.setUpdateAt(item.getUpdateAt());
            responseProductDTO.setAmount((typeVoucher != null)? typeVoucher.getAmount(): 0);
            responseProductDTOList.add(responseProductDTO);
        }

        Page<ResponseProductDTO> productPage = new PageImpl<>(responseProductDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iProductRepo.getProductsByIdOrder(idOrder).size());
        return productPage;
    }

    @Override
    public List<Product> getProductsByIdOrder(Long idOrder) {
        return iProductRepo.getProductsByIdOrder(idOrder);
    }

    @Override
    public Page<Product> getProductsByIdTypeVoucher(Long idTypeVoucher, int page, int size, String sort, String order) {
        return iProductRepo.getProductsByIdTypeVoucher(idTypeVoucher,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return iProductRepo.findById(id);
    }

    @Override
    public Optional<Product> getByInternalReference(Long internelReference) {
        return iProductRepo.getProductByInternalReference(internelReference);
    }

    @Override
    public void createProduct(Product product) {
        iProductRepo.save(product);
    }

    @Override
    public void deleteProduct(Product product) {
        iProductRepo.delete(product);
    }
}
