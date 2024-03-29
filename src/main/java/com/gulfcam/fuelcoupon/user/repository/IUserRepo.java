package com.gulfcam.fuelcoupon.user.repository;


import com.gulfcam.fuelcoupon.user.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IUserRepo extends IUserBaseRepo<Users> {

	Optional<Users> findByInternalReference(Long internalReference);

	List<Users> getUsersByIdStore(Long idStore);

	Optional<Users> findByPinCode(int pinCode);

	Optional<Users> findByEmail(String email);
	
	Optional<Users> findByTelephone(String tel);

	boolean existsByEmail(String email);

	boolean existsByInternalReference(Long internalReference);

	boolean existsByPinCode(int code);

	Page<Users> findDistinctByRolesIn(List<RoleUser> rolesManagers, Pageable p);

	Optional<Users> findByOtpCode(String code);

	Optional<Users> findByTokenAuth(String code);

	Page<Users> findTop20ByStatus(StatusUser status, Pageable p);

	List<Users> getUsersByTypeAccount(TypeAccount typeAccount);


    Page<Users> findAll(Specification<Users> specification, Pageable pageable);
}
