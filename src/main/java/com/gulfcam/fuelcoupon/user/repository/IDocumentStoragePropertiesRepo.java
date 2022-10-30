package com.gulfcam.fuelcoupon.user.repository;

import com.gulfcam.fuelcoupon.user.entity.DocumentCategorie;
import com.gulfcam.fuelcoupon.user.entity.DocumentStorageProperties;
import com.gulfcam.fuelcoupon.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IDocumentStoragePropertiesRepo extends JpaRepository<DocumentStorageProperties, Long> {
		
	@Query(value = "Select * from user_documents where user_user_id = ?1", nativeQuery = true)
	List<DocumentStorageProperties> checkDocumentByUserId(Long userId);

	DocumentStorageProperties findByUploadDir(String urlLink);

	DocumentStorageProperties findByUserAndCategories(Users users, DocumentCategorie categorie);
	

}
