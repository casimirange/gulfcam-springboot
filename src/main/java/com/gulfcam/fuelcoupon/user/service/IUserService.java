package com.gulfcam.fuelcoupon.user.service;

import com.gulfcam.fuelcoupon.user.dto.UserEditPasswordDto;
import com.gulfcam.fuelcoupon.user.entity.Users;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface IUserService {
	
	Map<String, Object> add(Users user);
	
	boolean existsByEmail(String email, Long id);
	
	boolean existsByIdGulfcam(String idGulfcam, Long id);
	
	Users getById(Long id);
	
	Optional<Users> getByTel(String tel);
	
	Optional<Users> getByEmail(String email);
	
	Users editToken(Long id, String token);
	
	Users editEmail(Long id, String email);

	Users editCountry(Long id,String country_code);

	
	Users editStatus(Long id, Long statusID);
	
	String editPassword(Users user, UserEditPasswordDto userEditPasswordDto);

	boolean existsByTel(String tel, Long id);

	Users editProfil(Users user, Long updateRoleId, Long addRoleId);

	void deleteUser(Long userId);

	Users updateAuthToken(Long id, String token);

	Users updateOtpCode(Long id, String code);

   Users  addCarriere(List<String> carriere, Long user_id);

   Users  addCarriereImport(List<Long> carriere, Long user_id);

	Users editUser(Long userId, Users u);

	void editTokenFcm(Long userId, String tokenFcm);

	Users getByOtpCode(String code);

	Users getNewCodeVerificationAccount(String login);

	Users checkUserAndGenerateCode(String login);

	Users resetPassword(Users user, String password);

	Optional<Users> getUserByUniqueConstraints(Users u);

	Page<Users> getUsers(int page, int size, String sort, String order);

	Page<Users> get20Users(int parseInt, int parseInt2, String sort, String order);

	List<Users> getUsers();

	void updateDateLastLoginUser(Long id_user);

	void updateFistLogin(Long user_id);

	Users updateImageUrl(Long id_user, String imageUrl);

   void checkIfUserSignContract(Long users_id);

	Users lockAndUnlockUsers(Long id_user, boolean status);
}
