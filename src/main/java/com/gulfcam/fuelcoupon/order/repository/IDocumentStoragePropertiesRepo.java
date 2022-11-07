package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.DocumentStorageProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IDocumentStoragePropertiesRepo extends JpaRepository<DocumentStorageProperties, Long> {

    @Query(value = "Select * from order_documents where id_order = ?1 and document_type = ?2", nativeQuery = true)
    DocumentStorageProperties checkDocumentByOrderId(Long idOrder, String docType);

    @Query(value = "Select file_name from order_documents a where id_order = ?1 and document_type = ?2", nativeQuery = true)
    String getUploadDocumnetPath(Long idOrder, String docType);
}
