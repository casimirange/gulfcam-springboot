package com.gulfcam.fuelcoupon.order.service;

import com.gulfcam.fuelcoupon.order.dto.ResponseProductDTO;
import com.gulfcam.fuelcoupon.order.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    Page<ResponseProductDTO> getAllProducts(int page, int size, String sort, String order);
    Page<ResponseProductDTO> getProductsByIdOrder(Long idOrder, int page, int size, String sort, String order);
    List<Product> getProductsByIdOrder(Long idOrder);
    Page<Product> getProductsByIdTypeVoucher(Long idClient, int page, int size, String sort, String order);
    Optional<Product> getProductById(Long id);
    Optional<Product> getByInternalReference(Long internelReference);
    void createProduct(Product product);
    void deleteProduct(Product product);

}
