package com.gulfcam.fuelcoupon.user.repository;

import com.gulfcam.fuelcoupon.user.entity.DocumentCategorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDocumentCategorieRepo extends JpaRepository<DocumentCategorie,Long> {
    DocumentCategorie findByNameFrIgnoreCaseAndNameEnIgnoreCaseAndNameEsIgnoreCase(String fr,String en,String es);
}
