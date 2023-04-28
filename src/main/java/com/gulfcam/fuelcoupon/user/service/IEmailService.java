package com.gulfcam.fuelcoupon.user.service;


import com.gulfcam.fuelcoupon.user.dto.EmailDto;

public interface IEmailService {

	void sendEmail(EmailDto emailDto);

}
