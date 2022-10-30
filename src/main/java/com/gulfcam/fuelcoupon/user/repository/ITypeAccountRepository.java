package com.gulfcam.fuelcoupon.user.repository;

import com.gulfcam.fuelcoupon.user.entity.ETypeAccount;
import com.gulfcam.fuelcoupon.user.entity.TypeAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITypeAccountRepository extends JpaRepository<TypeAccount,Long> {
    TypeAccount findByName(ETypeAccount name);
}
