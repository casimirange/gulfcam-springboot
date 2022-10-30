package com.gulfcam.fuelcoupon.user.service;

import com.gulfcam.fuelcoupon.user.entity.DocumentCategorie;
import com.gulfcam.fuelcoupon.user.entity.DocumentStorageProperties;

import java.util.List;

public interface IDocumentService {

    DocumentStorageProperties addDocument(DocumentStorageProperties doc);

    DocumentCategorie getOneDocumentCategorie(Long id_cat);

    List<DocumentCategorie> listcategorieDoc();

    List<DocumentStorageProperties> listDocumentByUsers(Long id_user);

    void deleteOneDocument(String link);

}
