
package com.gulfcam.fuelcoupon.authentication.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gulfcam.fuelcoupon.user.entity.StatusUser;
import com.gulfcam.fuelcoupon.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {

	private Long id;

	private String username;

	private String email;

	private Collection<? extends GrantedAuthority> roles;
	
	private StatusUser status;
	
	private String token;

	@JsonIgnore
	private String password;
	
	private boolean using2FA;

	public static UserDetailsImpl build(Users user) {
		List<GrantedAuthority> roles = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
		return new UserDetailsImpl(user.getUserId(), user.getIdGulfcam(), user.getEmail(), roles, user.getStatus(),
				user.getTokenAuth(), user.getPassword(),user.isUsing2FA());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles();
	}
}
