package com.gulfcam.fuelcoupon;


import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.order.entity.DocumentStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableConfigurationProperties({DocumentStorageProperties.class})
@Configuration
@EnableAsync
@Slf4j
public class FuelcouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuelcouponApplication.class, args);

//		AESUtil aesUtil = new AESUtil();
//		String key = "0e7cef307ba3195eef26fe5c1c9497f5";
//
//		String idCarton = "9bc5eb9dca3c35b74f1db38d509bea60e60f73df40710519b34edcd9477766524ef172bd117002764ee771ec3fd9b653LQtQo m81C9vhzuQFNDY1g==";
//		String idStoreHouseSell = "0ABD07CC626B20DB06F7783EBDAE62A790A7B98D6FDD2C136AF0EBE762F0314B751EA71D7BDC9EEF4C1E994B823B0703mxJV4N60aPVSJL3hknjNJA==";
//
//		log.info("idCarton: "+aesUtil.decrypt(key, idCarton));
//		log.info("idS: "+aesUtil.decrypt(key, idStoreHouseSell));

	}

	@Bean
	public TaskExecutor threadPoolTaskExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setQueueCapacity(100);
		executor.setMaxPoolSize(2);
		executor.setCorePoolSize(2);
		executor.setThreadNamePrefix("poolThread-");
		executor.initialize();

		return executor;
	}

	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer(){
		return factory -> {
			try {
				factory.setAddress(InetAddress.getByName("0.0.0.0"));
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		};
	}

}
