package com.gulfcam.fuelcoupon.user.repository;

import com.gulfcam.fuelcoupon.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IUserBaseRepo<T extends Users> extends JpaRepository<T, Long> {
	
}
