package com.gulfcam.fuelcoupon.user.repository;

import com.gulfcam.fuelcoupon.user.entity.ETypeAccount;
import com.gulfcam.fuelcoupon.user.entity.TypeAccount;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITypeAccountRepository extends JpaRepository<TypeAccount,Long> {
    Optional<TypeAccount> findByName(ETypeAccount name);
}
