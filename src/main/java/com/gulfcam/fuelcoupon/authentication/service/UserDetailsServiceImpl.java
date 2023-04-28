
package com.gulfcam.fuelcoupon.authentication.service;


import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private IUserRepo userRepo;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) {
		Users user = userRepo.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("l'utilisateur " + username + " n'a pas été trouvé"));
		return UserDetailsImpl.build(user);
	}

}
