package com.gulfcam.fuelcoupon.user.service;

import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.user.dto.UserEditPasswordDto;
import com.gulfcam.fuelcoupon.user.entity.*;
import com.gulfcam.fuelcoupon.user.repository.IOldPasswordRepo;
import com.gulfcam.fuelcoupon.user.repository.IRoleUserRepo;
import com.gulfcam.fuelcoupon.user.repository.IStatusUserRepo;
import com.gulfcam.fuelcoupon.user.repository.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	private IStatusUserRepo statusRepo;

	@Autowired
	private IRoleUserRepo roleRepo;

	@Autowired
	private IOldPasswordRepo oldPasswordRepo;

	@Autowired
    PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	private ResourceBundleMessageSource messageSource;

//	@Override
//	public Optional<Users> getByIdJobEtrouve(String username) {
//		Optional<Users> users = userRepo.findByIdJobEtrouve(username);
//		if(users.isPresent()) {
//			if(users.get().getClass().equals(Particular.class)) {
//				checkIfUserSignContract(users.get().getUserId());
//			}
//		}
//		return userRepo.findByIdJobEtrouve(username);
//	}
	
	@Override
	@Transactional
	public Map<String, Object> add(Users u) {
		String password = RandomStringUtils.random(15, 35, 125, true, true, null, new SecureRandom());
		Users user = new Users(u.getIdGulfcam(), u.getEmail(), encoder.encode(password));
		Set<RoleUser> rolesList = u.getRoleNames().stream()
				.map(roleName -> roleRepo.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Role name " + roleName + " not found")))
				.collect(Collectors.toSet());
		user.setRoles(rolesList);
		StatusUser status = statusRepo.findByName(EStatusUser.USER_ENABLED);
		user.setStatus(status);
		user.setTokenAuth(null);
		user.setCreatedDate(LocalDate.now());
		OldPassword oldPassword = oldPasswordRepo.save(new OldPassword(encoder.encode(password)));
		user.setOldPasswords(Arrays.asList(oldPassword));
		userRepo.save(user);
		Map<String, Object> userAndPasswordNotEncoded = new HashMap<>();
		userAndPasswordNotEncoded.put("user", user);
		userAndPasswordNotEncoded.put("password", password);
		return userAndPasswordNotEncoded;
	}

	@Override
	public Users getById(Long id) {
		Users user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));

		return user;
	}

	@Override
	public Optional<Users> getByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public Optional<Users> getByTel(String tel) {
		Optional<Users> user = userRepo.findByTel1(tel);
		if (user.isEmpty()) {
			return userRepo.findByTel2(tel);
		}
		return user;
	}

	@Override
	public Users getByOtpCode(String code) {
		return userRepo.findByOtpCode(code)
				.orElseThrow(() -> new ResourceNotFoundException("User code OTP " + code + " not found"));
	}

	@Override
	@Transactional
	public Users editUser(Long userId, Users u) {
		Users user = getById(userId);
		user.setEmail(u.getEmail());
		user.setTelephone(u.getTelephone());
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public Users editToken(Long id, String token) {
		Users user = getById(id);
		user.setTokenAuth(token);
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public void editTokenFcm(Long id, String tokeFcm) {
		Users user = getById(id);
		user.setNotificationKey(tokeFcm);
		userRepo.save(user);
	}


	@Override
	@Transactional
	public Users editStatus(Long id, Long statusId) {
		Users user = getById(id);
		StatusUser status = statusRepo.findById(statusId)
				.orElseThrow(() -> new ResourceNotFoundException("Status id " + id + " not found"));
		user.setStatus(status);
		if (user.getStatus().equals(status)) {
			return user;
		}
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public Users editEmail(Long id, String email) {
		Users user = getById(id);
		user.setEmail(email);
		return userRepo.save(user);
	}

	@Override
	public Users editCountry(Long id, String country_code) {
		return null;
	}

	@Override
	public Optional<Users> getUserByUniqueConstraints(Users u) {
		Optional<Users> user = userRepo.findByIdJobEtrouveIgnoreCase(u.getIdGulfcam());
		if(user.isPresent()) {
			return user;
		}
		if(u.getEmail() != null) {
			user = userRepo.findByEmail(u.getEmail());
			if(user.isPresent()) {
				return user;
			}
		}
		if(u.getTelephone() != null) {
			user = userRepo.findByTel1("+" + u.getTelephone());
			if(user.isPresent()) {
				return user;
			}
		}
		return Optional.empty();
	}

	public boolean existsByEmail(String email, Long id) {
		if (email == null || email.isEmpty()) {
			return false;
		}
		Optional<Users> user = userRepo.findByEmail(email);
		return checkOwnerIdentity(id, user);
	}

	@Override
	public boolean existsByIdGulfcam(String username, Long id) {
		Optional<Users> user = userRepo.findByIdJobEtrouveIgnoreCase(username);
		return checkOwnerIdentity(id, user);
	}

	@Override
	public boolean existsByTel(String tel, Long id) {
		if (tel == null || tel.isEmpty()) {
			return false;
		}
		Optional<Users> user = userRepo.findByTel1(tel);
		if (user.isEmpty()) {
			user = userRepo.findByTel2(tel);
		}
		return checkOwnerIdentity(id, user);
	}

	@Override
	@Transactional
	public Users checkUserAndGenerateCode(String login) {
		Users user;
		if (login.contains("@")) {
			user = userRepo.findByEmail(login).orElseThrow(() -> new ResourceNotFoundException(
					messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
			String token = jwtUtils.generateJwtToken(user.getIdGulfcam(), jwtUtils.getExpirationEmailResetPassword(),
					jwtUtils.getSecretBearerToken(),true);
			user.setTokenAuth(token);
		} else {
			user = getByTel(login).orElseThrow(() -> new ResourceNotFoundException(
					messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
			String codeOtp = String.valueOf(jwtUtils.generateOtpCode());
			user.setOtpCode(codeOtp);
			user.setOtpCodeCreatedAT(LocalDateTime.now());
		}
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public Users resetPassword(Users user, String password) {
		user.setPassword(encoder.encode(password));
		OldPassword oldPassword = oldPasswordRepo.save(new OldPassword(encoder.encode(password)));
		user.getOldPasswords().add(oldPassword);
		user.setOtpCode(null);
		user.setOtpCodeCreatedAT(null);
		user.setTokenAuth(null);
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public String editPassword(Users user, UserEditPasswordDto u) {
		OldPassword oldPassword = oldPasswordRepo.save(new OldPassword(encoder.encode(u.getPassword())));
		user.getOldPasswords().add(oldPassword);
		user.setPassword(encoder.encode(u.getPassword()));
		user.setTokenAuth(null);
		userRepo.save(user);
		return "Mot de passe changé avec succès";
	}

	@Override
	@Transactional
	public Users editProfil(Users user, Long updateRoleId, Long addRoleId) {
		Set<RoleUser> rolesList = user.getRoles();
		RoleUser addRoleUser = roleRepo.findById(addRoleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role id " + addRoleId + " not found"));
		if (updateRoleId != 0) {
			RoleUser updateRoleUser = roleRepo.findById(updateRoleId)
					.orElseThrow(() -> new ResourceNotFoundException("Role id " + updateRoleId + " not found"));
			if (rolesList.contains(updateRoleUser)) {
				rolesList.remove(updateRoleUser);
				rolesList.add(addRoleUser);
			} else {
				throw new ResourceNotFoundException("User doesn't have profil " + updateRoleUser.getName().toString());
			}
		} else {
			rolesList.add(addRoleUser);
		}
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		userRepo.deleteById(userId);
	}

	private boolean checkOwnerIdentity(Long id, Optional<Users> user) {
		boolean taken = false;
		if (!user.isPresent()) {
			return taken;
		}
		if (id != null) {
			if (id.equals(user.get().getUserId())) {
				return taken;
			} else {
				taken = true;
				return taken;
			}
		} else {
			taken = true;
			return taken;
		}
	}

	private List<RoleUser> getRolesManagers() {
		List<ERole> rolesNames = new ArrayList<>();
		rolesNames.add(ERole.ROLE_SUPERADMIN);
		rolesNames.add(ERole.ROLE_ADMIN);
		return rolesNames.stream()
				.map(roleName -> roleRepo.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Role name " + roleName + " not found")))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Users getNewCodeVerificationAccount(String login) {
		Optional<Users> user = getByTel(login);
		if (user.isPresent()) {
			String otpCode = String.valueOf(jwtUtils.generateOtpCode());
			user.get().setOtpCode(otpCode);
			user.get().setOtpCodeCreatedAT(LocalDateTime.now());

		} else {
			user = userRepo.findByEmail(login);
			String token = jwtUtils.generateJwtToken(user.get().getIdGulfcam(), jwtUtils.getExpirationEmailVerifToken(),
					jwtUtils.getSecretBearerToken(),true);
			user.get().setTokenAuth(token);
		}
		return userRepo.save(user.get());
	}

	@Override
	public Page<Users> getUsers(int page, int size, String sort, String order) {
		return userRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
	}

	@Override
	public Page<Users> get20Users(int page, int size, String sort, String order) {
		StatusUser status = statusRepo.findByName(EStatusUser.USER_ENABLED);
		return userRepo.findTop20ByStatus(status , PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
	}

	@Override
	public List<Users> getUsers() {
		return userRepo.findAll();
	}

	@Override
	public void updateDateLastLoginUser(Long id_user) {
		 Users u = getById(id_user);
		 u.setDateLastLogin(LocalDateTime.now());
		 userRepo.save(u);
	}

	@Override
	public Users updateImageUrl(Long id_user, String imageUrl) {
		Users u = getById(id_user);
		u.setImageUrl(imageUrl);
		return userRepo.save(u);
	}

	@Override
	public void checkIfUserSignContract(Long users_id) {

	}


	@Override
	public Users updateAuthToken(Long id, String token) {
		Optional<Users> users = userRepo.findById(id);
		if(!users.isPresent() || users.get().isDelete()) {
			throw new ResourceNotFoundException("User  not found");
		}
		users.get().setTokenAuth(token);
		return userRepo.save(users.get());
	}

	@Override
	public Users updateOtpCode(Long id, String code) {
		Optional<Users> users = userRepo.findById(id);
		if(!users.isPresent() || users.get().isDelete()) {
			throw new ResourceNotFoundException("User  not found");
		}
		users.get().setOtpCode(code);
		users.get().setOtpCodeCreatedAT(LocalDateTime.now());
		return userRepo.save(users.get());
	}

	@Override
	public Users addCarriere(List<String> carriere, Long user_id) {
		return null;
	}

	@Override
	public Users addCarriereImport(List<Long> carriere, Long user_id) {
		return null;
	}

	@Override
	public void updateFistLogin(Long user_id) {
		Users u = userRepo.getById(user_id);
		if(u.getOldPasswords().size() <=1 && u.getCreatedDate().isAfter(LocalDate.of(2021, Month.AUGUST,01))) {
			u.setFirstConnection(true);
		} else  {
			u.setFirstConnection(false);
		}
		userRepo.save(u);
	}

   @Override
  public Users lockAndUnlockUsers(Long id_user, boolean status) {
		Users u = getById(id_user);
		if(status == true) {
			StatusUser statusUser = statusRepo.findByName(EStatusUser.USER_ENABLED);
			u.setStatus(statusUser);
		} else {
			StatusUser statusUser = statusRepo.findByName(EStatusUser.USER_DISABLED);
			u.setStatus(statusUser);
		}
		return  userRepo.save(u);
	}
}
