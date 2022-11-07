package com.gulfcam.fuelcoupon.order.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IDocumentStorageService {
    String storeFile(MultipartFile file, Long idOrder, String docType);

    Resource loadFileAsResource(String fileName) throws Exception;

    String getDocumentName(Long customerId, String docType);
}
