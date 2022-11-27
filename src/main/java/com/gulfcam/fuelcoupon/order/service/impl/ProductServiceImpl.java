package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.order.entity.Product;
import com.gulfcam.fuelcoupon.order.repository.IProductRepo;
import com.gulfcam.fuelcoupon.order.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    @Autowired
    IProductRepo iProductRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Product> getAllProducts(int page, int size, String sort, String order) {
        return iProductRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Product> getProductsByIdOrder(Long idOrder, int page, int size, String sort, String order) {
        return iProductRepo.getProductsByIdOrder(idOrder,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
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
