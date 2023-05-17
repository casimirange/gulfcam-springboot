package com.gulfcam.fuelcoupon.user.service;

import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.cryptage.CryptageImpl;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.store.service.IStoreService;
import com.gulfcam.fuelcoupon.user.dto.ResponseUsersDTO;
import com.gulfcam.fuelcoupon.user.dto.UserEditPasswordDto;
import com.gulfcam.fuelcoupon.user.entity.*;
import com.gulfcam.fuelcoupon.user.repository.*;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
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
	private IStoreService iStoreService;

	@Autowired
	private IOldPasswordRepo oldPasswordRepo;

	@Autowired
	ITypeAccountRepository typeAccountRepo;

	@Autowired
    PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Autowired
	private CryptageImpl cryptage;

	@Value("${app.key}")
	private String AES_KEY;

	private AESUtil aes = new AESUtil();

	@Override
	@Transactional
	public Map<String, Object> add(Users u) {
		Users user = new Users(u.getInternalReference(), u.getEmail(), u.getPassword());

		StatusUser status = statusRepo.findByName(EStatusUser.USER_ENABLED);
		user.setStatus(status);
		user.setTokenAuth(null);
		user.setCreatedDate(LocalDateTime.now());
//		OldPassword oldPassword = oldPasswordRepo.save(new OldPassword(u.getPassword()));
//		user.setOldPasswords(Arrays.asList(oldPassword));
		user.setFirstName(u.getFirstName());
		user.setLastName(u.getLastName());
		user.setTelephone(u.getTelephone());
		user.setPinCode(u.getPinCode());
		user.setPosition(u.getPosition());
		user.setIdStore(u.getIdStore());
		user.setRoles(u.getRoles());
		user.setTypeAccount(u.getTypeAccount());
		user.setCreatedDate(LocalDateTime.now());
		userRepo.save(user);
		Map<String, Object> userAndPasswordNotEncoded = new HashMap<>();
		userAndPasswordNotEncoded.put("user", user);
		userAndPasswordNotEncoded.put("password", u.getPassword());
		return userAndPasswordNotEncoded;
	}

	@Override
	@Transactional
	public Map<String, Object> modify(Users u) {
		userRepo.save(u);
		Map<String, Object> userAndPasswordNotEncoded = new HashMap<>();
		userAndPasswordNotEncoded.put("user", u);
		userAndPasswordNotEncoded.put("password", u.getPassword());
		return userAndPasswordNotEncoded;
	}

	@Override
	public Users getById(Long id) {
		Users user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
//		user.setTelephone(aes.encrypt(AES_KEY, user.getTelephone()));
//		user.setEmail(aes.encrypt(AES_KEY, user.getEmail()));
//		user.setFirstName(aes.encrypt(AES_KEY, user.getFirstName()));
//		user.setLastName(aes.encrypt(AES_KEY, user.getLastName()));
//		user.setInternalReference(Long.parseLong(aes.encrypt(AES_KEY, user.getInternalReference().toString())));
//		user.setInternalReference(Long.parseLong(aes.encrypt(AES_KEY, user.getInternalReference().toString())));
		return user;
	}

	@Override
	public Users getByInternalReference(Long internalReference) {
		Users user = userRepo.findByInternalReference(internalReference).orElseThrow(() -> new ResourceNotFoundException(
				messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));

		return user;
	}

	@Override
	public Optional<Users> getByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public Optional<Users> getByPinCode(int code) {
		return userRepo.findByPinCode(code);
	}

	@Override
	public Optional<Users> getByTelephone(String tel) {
		Optional<Users> user = userRepo.findByTelephone(tel);
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
		user.setFirstName(u.getFirstName());
		user.setLastName(u.getLastName());
		user.setTelephone(u.getTelephone());
		user.setPinCode(u.getPinCode());
		user.setPosition(u.getPosition());
		user.setIdStore(u.getIdStore());
		user.setTypeAccount(u.getTypeAccount());
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
	public Optional<Users> getUserByUniqueConstraints(Users u) {
		Optional<Users> user = userRepo.findByInternalReference(u.getInternalReference());
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
			user = userRepo.findByTelephone("+" + u.getTelephone());
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
	public boolean existsByInternalReference(Long internalReference) {
		return userRepo.existsByInternalReference(internalReference);
	}


	@Override
	public boolean existsByPinCode(int pinCode) {
		return userRepo.existsByPinCode(pinCode);
	}

	@Override
	public boolean existsByTelephone(String tel, Long id) {
		if (tel == null || tel.isEmpty()) {
			return false;
		}
		Optional<Users> user = userRepo.findByTelephone(tel);
		return checkOwnerIdentity(id, user);
	}

	@Override
	@Transactional
	public Users checkUserAndGenerateCode(String login) {
		Users user;
		if (login.contains("@")) {
			user = userRepo.findByEmail(login).orElseThrow(() -> new ResourceNotFoundException(
					messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
			String token = jwtUtils.generateJwtToken(user.getEmail(), jwtUtils.getExpirationEmailResetPassword(),
					jwtUtils.getSecretBearerToken(),true);
			user.setTokenAuth(token);
		} else {
			user = getByPinCode(Integer.parseInt(login)).orElseThrow(() -> new ResourceNotFoundException(
					messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
			String token = jwtUtils.generateJwtToken(user.getEmail(), jwtUtils.getExpirationEmailResetPassword(),
					jwtUtils.getSecretBearerToken(),true);
			user.setTokenAuth(token);
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
		OldPassword oldPassword = oldPasswordRepo.save(new OldPassword(encoder.encode(aes.decrypt(AES_KEY, u.getPassword()))));
		user.getOldPasswords().add(oldPassword);
		user.setPassword(encoder.encode(aes.decrypt(AES_KEY, u.getPassword())));
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
		Optional<Users> user = getByEmail(login);
		if (user.isPresent()) {
			String otpCode = String.valueOf(jwtUtils.generateOtpCode());
			user.get().setOtpCode(otpCode);
			user.get().setOtpCodeCreatedAT(LocalDateTime.now());

		} else {
			user = userRepo.findByEmail(login);
			String token = jwtUtils.generateJwtToken(user.get().getEmail(), jwtUtils.getExpirationEmailVerifToken(),
					jwtUtils.getSecretBearerToken(),true);
			user.get().setTokenAuth(token);
		}
		return userRepo.save(user.get());
	}

	@Override
	public Page<ResponseUsersDTO> getUsers(int page, int size, String sort, String order) {
		List<Users> usersList = userRepo.findAll();

		ResponseUsersDTO responseUsersDTO;
		List<ResponseUsersDTO> responseUsersDTOList = new ArrayList<>();

		for (Users user: usersList){
			responseUsersDTO = new ResponseUsersDTO();
			responseUsersDTO.setStatus(user.getStatus());
			responseUsersDTO.setUserId(user.getUserId().toString());
			responseUsersDTO.setEmail(user.getEmail());
			responseUsersDTO.setRoleNames(user.getRoleNames());
			responseUsersDTO.setFirstName(user.getFirstName());
			responseUsersDTO.setLastName(user.getLastName());
			responseUsersDTO.setIdStore(user.getIdStore());
			responseUsersDTO.setPosition(user.getPosition());
			responseUsersDTO.setPinCode(user.getPinCode());
			responseUsersDTO.setRoles(user.getRoles());
			responseUsersDTO.setTelephone(user.getTelephone());
			responseUsersDTO.setTypeAccount(user.getTypeAccount());
			responseUsersDTO.setOtpCode(user.getOtpCode());
			responseUsersDTO.setStore((user.getIdStore() == null)? null: iStoreService.getByInternalReference(user.getIdStore()).get());
			responseUsersDTO.setNameStore((user.getIdStore() == null)? null: iStoreService.getByInternalReference(user.getIdStore()).get().getLocalization());
			responseUsersDTO.setInternalReference(user.getInternalReference().toString());
			responseUsersDTO.setCreatedDate(user.getCreatedDate());
			responseUsersDTO.setDateLastLogin(user.getDateLastLogin());
			responseUsersDTOList.add(responseUsersDTO);

		}
		Page<ResponseUsersDTO> responseUsersDTOPage = new PageImpl<>(responseUsersDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseUsersDTOList.size());
		return responseUsersDTOPage;
	}

	@Override
	public Page<ResponseUsersDTO> filtres(String statusName, String typeAccount, String firstName, String lastName, String idStore, int page, int size, String sort, String order) {

		Specification<Users> specification = ((root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (firstName != null && !firstName.isEmpty()){
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName + "%"));
			}

			if (lastName != null && !lastName.isEmpty()){
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName + "%"));
			}

			if (idStore != null && !idStore.isEmpty()){
				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idStore")), Long.parseLong(idStore) ));
			}

			if (typeAccount != null && !typeAccount.isEmpty()){
				Optional<TypeAccount> typeVoucher = typeAccountRepo.findByName(ETypeAccount.valueOf(typeAccount));
				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("typeAccount")), typeVoucher.map(TypeAccount::getId).orElse(null)));
			}

			if (statusName != null && !statusName.isEmpty()){
				StatusUser status = statusRepo.findByName(EStatusUser.valueOf(statusName.toUpperCase()));
				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status));
			}
			return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
		});

		Page<Users> users = userRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

		ResponseUsersDTO responseUsersDTO;
		List<ResponseUsersDTO> responseUsersDTOList = new ArrayList<>();

//		for (Users user: users){
//			responseUsersDTO = new ResponseUsersDTO();
//			responseUsersDTO.setStatus(user.getStatus());
//			responseUsersDTO.setUserId(aes.encrypt(AES_KEY, user.getUserId().toString()));
//			responseUsersDTO.setEmail(aes.encrypt(AES_KEY, user.getEmail()));
//			responseUsersDTO.setRoleNames(user.getRoleNames());
//			responseUsersDTO.setFirstName(aes.encrypt(AES_KEY, user.getFirstName()));
//			responseUsersDTO.setLastName(aes.encrypt(AES_KEY, user.getLastName()));
////			responseUsersDTO.setIdStore(aes.encrypt(AES_KEY, user.getIdStore()));
//			responseUsersDTO.setPosition(aes.encrypt(AES_KEY, user.getPosition()));
//			responseUsersDTO.setPinCode(user.getPinCode());
//			responseUsersDTO.setRoles(user.getRoles());
//			responseUsersDTO.setTelephone(aes.encrypt(AES_KEY, user.getTelephone()));
//			responseUsersDTO.setTypeAccount(user.getTypeAccount());
//			responseUsersDTO.setOtpCode(aes.encrypt(AES_KEY, user.getOtpCode()));
//			responseUsersDTO.setStore((user.getIdStore() == null)? null: iStoreService.getByInternalReference(user.getIdStore()).get());
//			responseUsersDTO.setNameStore((user.getIdStore() == null)? null: aes.encrypt(AES_KEY, iStoreService.getByInternalReference(user.getIdStore()).get().getLocalization()));
//			responseUsersDTO.setInternalReference(aes.encrypt(AES_KEY, user.getInternalReference().toString()));
//			responseUsersDTO.setCreatedDate(user.getCreatedDate());
//			responseUsersDTO.setDateLastLogin(user.getDateLastLogin());
//			responseUsersDTOList.add(responseUsersDTO);
//
//		}
		for (Users user: users){
			responseUsersDTO = new ResponseUsersDTO();
			responseUsersDTO.setStatus(user.getStatus());
			responseUsersDTO.setUserId(user.getUserId().toString());
			responseUsersDTO.setEmail(user.getEmail());
			responseUsersDTO.setRoleNames(user.getRoleNames());
			responseUsersDTO.setFirstName(user.getFirstName());
			responseUsersDTO.setLastName(user.getLastName());
//			responseUsersDTO.setIdStore(aes.encrypt(AES_KEY, user.getIdStore()));
			responseUsersDTO.setPosition(user.getPosition());
			responseUsersDTO.setPinCode(user.getPinCode());
			responseUsersDTO.setRoles(user.getRoles());
			responseUsersDTO.setTelephone(user.getTelephone());
			responseUsersDTO.setTypeAccount(user.getTypeAccount());
			responseUsersDTO.setOtpCode(user.getOtpCode());
			responseUsersDTO.setStore((user.getIdStore() == null)? null: iStoreService.getByInternalReference(user.getIdStore()).get());
			responseUsersDTO.setNameStore((user.getIdStore() == null)? null: iStoreService.getByInternalReference(user.getIdStore()).get().getLocalization());
			responseUsersDTO.setInternalReference(user.getInternalReference().toString());
			responseUsersDTO.setCreatedDate(user.getCreatedDate());
			responseUsersDTO.setDateLastLogin(user.getDateLastLogin());
			responseUsersDTOList.add(responseUsersDTO);

		}
		return new PageImpl<>(responseUsersDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), users.getTotalElements());

	}

	@Override
	public List<Users> getUsersByIdStore(Long idStore) {
		return userRepo.getUsersByIdStore(idStore);
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
	public List<Users> getUsersByTypeAccount(TypeAccount typeAccount) {
		return userRepo.getUsersByTypeAccount(typeAccount);
	}

	@Override
	public void updateDateLastLoginUser(Long id_user) {
		 Users u = getById(id_user);
		 u.setDateLastLogin(LocalDateTime.now());
		 userRepo.save(u);
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
	public void updateFistLogin(Long user_id) {
		Users u = userRepo.getById(user_id);
		if(u.getOldPasswords().size() <=1 && u.getCreatedDate().isAfter(LocalDateTime.of(2022, Month.NOVEMBER,01, 23, 01, 01))) {
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
