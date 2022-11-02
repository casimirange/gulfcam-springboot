package com.gulfcam.fuelcoupon.client.repository;

import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITypeClientRepo extends JpaRepository<TypeClient,Long> {
    Optional<TypeClient> findByName(String name);
}
