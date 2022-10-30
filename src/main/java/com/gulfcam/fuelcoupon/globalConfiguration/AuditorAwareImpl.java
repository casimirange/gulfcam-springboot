package com.gulfcam.fuelcoupon.globalConfiguration;


import com.gulfcam.fuelcoupon.authentication.service.UserDetailsImpl;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Users> {

	@Autowired
	private IUserRepo userRepo;

	@Override
	public Optional<Users> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getName().equals("anonymousUser")) {
			return Optional.empty();
		}
		UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
		return userRepo.findById(currentUser.getId());
	}

}
