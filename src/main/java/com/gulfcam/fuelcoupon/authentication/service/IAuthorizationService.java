package com.gulfcam.fuelcoupon.authentication.service;



import com.gulfcam.fuelcoupon.user.entity.Users;

import java.util.List;

public interface IAuthorizationService {

	Users getUserInContextApp();


	List<String> getUserRoles();

}
