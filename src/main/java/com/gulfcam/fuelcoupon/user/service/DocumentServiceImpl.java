package com.gulfcam.fuelcoupon.user.service;

import com.gulfcam.fuelcoupon.user.entity.DocumentCategorie;
import com.gulfcam.fuelcoupon.user.entity.DocumentStorageProperties;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.repository.IDocumentCategorieRepo;
import com.gulfcam.fuelcoupon.user.repository.IDocumentStoragePropertiesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
@Service
@Transactional
public class DocumentServiceImpl implements IDocumentService {
    @Autowired
    ResourceBundleMessageSource messageSource;
    @Autowired
    IUserService userService;

    @Autowired
    IDocumentStoragePropertiesRepo documentStoragePropertiesRepo;

    @Autowired
    IDocumentCategorieRepo documentCategorieRepo;

    @Override
    public DocumentStorageProperties addDocument(DocumentStorageProperties doc) {
        doc.setCreatedAt(LocalDate.now());
        return documentStoragePropertiesRepo.save(doc);
    }

    @Override
    public DocumentCategorie getOneDocumentCategorie(Long id_cat) {
        return documentCategorieRepo.findById(id_cat).orElseThrow(() -> new ResourceNotFoundException("the category of the document whose id is: "+id_cat+" not found"));
    }

    @Override
    public List<DocumentCategorie> listcategorieDoc() {
        return documentCategorieRepo.findAll();
    }

    @Override
    public List<DocumentStorageProperties> listDocumentByUsers(Long id_user) {
        Users users = userService.getById(id_user);
        return documentStoragePropertiesRepo.checkDocumentByUserId(users.getUserId());
    }

    @Override
    public void deleteOneDocument(String linkUrl) {
     DocumentStorageProperties documentStorageProperties = documentStoragePropertiesRepo.findByUploadDir(linkUrl);
      if(documentStorageProperties == null) {
          new ResourceNotFoundException(messageSource.getMessage("message.file_not_found", null, LocaleContextHolder.getLocale()));
      }
     documentStoragePropertiesRepo.delete(documentStorageProperties);
    }
}
