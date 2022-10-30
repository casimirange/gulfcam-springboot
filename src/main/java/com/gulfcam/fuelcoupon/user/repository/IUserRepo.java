package com.gulfcam.fuelcoupon.user.repository;


import com.gulfcam.fuelcoupon.user.entity.RoleUser;
import com.gulfcam.fuelcoupon.user.entity.StatusUser;
import com.gulfcam.fuelcoupon.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IUserRepo extends IUserBaseRepo<Users> {
	
	Optional<Users> findByIdJobEtrouveIgnoreCase(String idJobEtrouve);
	
	Optional<Users> findByIdJobEtrouve(String idJobEtrouve);

	Optional<Users> findByEmail(String email);
	
	Optional<Users> findByTel1(String tel);
	
	Optional<Users> findByTel2(String tel2);

	boolean existsByIdJobEtrouve(String idJobEtrouve);

	boolean existsByEmail(String email);

	Page<Users> findDistinctByRolesIn(List<RoleUser> rolesManagers, Pageable p);




	Page<Users> findDistinctByRolesNotIn(List<RoleUser> rolesManagers, Pageable p);

	Optional<Users> findByOtpCode(String code);

	Optional<Users> findByTokenAuth(String code);

	Page<Users> findTop20ByStatus(StatusUser status, Pageable p);


	List<Users> findByDateLastLoginIsAfterOrderByDateLastLoginDesc(LocalDateTime localDateTime);



}
