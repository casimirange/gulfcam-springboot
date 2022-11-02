package com.gulfcam.fuelcoupon.client.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Random;

@Component
@Slf4j
@Getter
@Setter
public class ClientUtils {

	public Long generateInternalReference() {
		Long internalReference =  Long.parseLong((100 + new Random().nextInt(900)) + RandomStringUtils.random(4, 35, 125, false, true, null, new SecureRandom()));
		return internalReference;
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
