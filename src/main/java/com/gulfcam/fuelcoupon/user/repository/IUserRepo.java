package com.gulfcam.fuelcoupon.user.repository;


<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.user.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
=======
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import com.gulfcam.fuelcoupon.user.entity.RoleUser;
import com.gulfcam.fuelcoupon.user.entity.StatusUser;
import com.gulfcam.fuelcoupon.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
<<<<<<< HEAD
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6

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

<<<<<<< HEAD
<<<<<<< HEAD
	List<Users> getUsersByTypeAccount(TypeAccount typeAccount);


    Page<Users> findAll(Specification<Users> specification, Pageable pageable);
=======

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
}
