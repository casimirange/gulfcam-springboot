package com.gulfcam.fuelcoupon.order.service.impl;

import com.gulfcam.fuelcoupon.exception.DocumentsStorageException;
import com.gulfcam.fuelcoupon.order.entity.DocumentStorageProperties;
import com.gulfcam.fuelcoupon.order.entity.TypeDocument;
import com.gulfcam.fuelcoupon.order.repository.IDocumentStoragePropertiesRepo;
import com.gulfcam.fuelcoupon.order.service.IDocumentStorageService;
<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
<<<<<<< HEAD
@RequiredArgsConstructor
public class DocumentStorageServiceImpl implements IDocumentStorageService {

    private Path fileStorageLocation;
=======
public class DocumentStorageServiceImpl implements IDocumentStorageService {

    private final Path fileStorageLocation;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    @Autowired
    IDocumentStoragePropertiesRepo docStorageRepo;
    
    @Autowired
    public DocumentStorageServiceImpl(DocumentStorageProperties fileStorageProperties) {
        fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
<<<<<<< HEAD
        log.info("path: {}"+fileStorageLocation);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            throw new DocumentsStorageException("Could not create the directory where the uploaded files will be stored.", e);

        }
    }
    @Override
    @Transactional
    public String storeFile(MultipartFile file, Long idOrder, String docType, TypeDocument typeDocument) {
        // Normalize file name
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = "";

        try {
            // Check if the file's name contains invalid characters
            if(originalFilename.contains("..")) {
                throw new DocumentsStorageException("Sorry! Filename contains invalid path sequence " + originalFilename);
            }
            String fileExtension = "";
            try {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } catch (Exception e) {
                fileExtension = "";
            }
            fileName = idOrder + "_" + docType + fileExtension;
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            DocumentStorageProperties doc = docStorageRepo.checkDocumentByOrderId(idOrder, docType, typeDocument.getId());
            if(doc != null) {
                doc.setDocumentFormat(file.getContentType());
                doc.setFileName(fileName);
                doc.setType(typeDocument);
                docStorageRepo.save(doc);
            } else {
                DocumentStorageProperties newDoc = new DocumentStorageProperties();
                newDoc.setOrder(idOrder);
                newDoc.setDocumentFormat(file.getContentType());
                newDoc.setFileName(fileName);
                newDoc.setType(typeDocument);
                newDoc.setDocumentType(docType);
                docStorageRepo.save(newDoc);
            }
            return fileName;
        } catch (IOException e) {
            throw new DocumentsStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

    

    @Override
    @Transactional
    public Resource loadFileAsResource(String fileName) throws Exception {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }

        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    @Override
    public String getDocumentName(Long idOrder, String docType, Long type) {
        return docStorageRepo.getUploadDocumnetPath(idOrder, docType, type);
    }
}
