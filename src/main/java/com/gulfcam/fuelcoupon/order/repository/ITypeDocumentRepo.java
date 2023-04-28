package com.gulfcam.fuelcoupon.order.repository;

import com.gulfcam.fuelcoupon.order.entity.ETypeDocument;
import com.gulfcam.fuelcoupon.order.entity.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "typeDocument")
public interface ITypeDocumentRepo extends JpaRepository<TypeDocument, Long> {
	Optional<TypeDocument> findByName(ETypeDocument name);

}
