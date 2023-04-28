package com.gulfcam.fuelcoupon.user.repository;


import com.gulfcam.fuelcoupon.user.entity.ERole;
import com.gulfcam.fuelcoupon.user.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleUserRepo extends JpaRepository<RoleUser, Long> {
	Optional<RoleUser> findByName(ERole name);
}
