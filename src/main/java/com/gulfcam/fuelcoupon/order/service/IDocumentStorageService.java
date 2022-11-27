package com.gulfcam.fuelcoupon.order.service;

import com.gulfcam.fuelcoupon.order.entity.TypeDocument;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IDocumentStorageService {
    String storeFile(MultipartFile file, Long idOrder, String docType, TypeDocument typeDocument);

    Resource loadFileAsResource(String fileName) throws Exception;

    String getDocumentName(Long idOrder, String docType, Long type);
}
