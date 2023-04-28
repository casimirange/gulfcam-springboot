package com.gulfcam.fuelcoupon.user.repository;


import com.gulfcam.fuelcoupon.user.entity.OldPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOldPasswordRepo extends JpaRepository<OldPassword, Long> {

}
