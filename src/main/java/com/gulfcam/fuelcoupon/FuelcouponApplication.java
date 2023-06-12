package com.gulfcam.fuelcoupon;


import com.gulfcam.fuelcoupon.cryptage.AESUtil;
import com.gulfcam.fuelcoupon.order.entity.DocumentStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableConfigurationProperties({DocumentStorageProperties.class})
@Configuration
@EnableAsync
@Slf4j
public class FuelcouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuelcouponApplication.class, args);

		AESUtil aes = new AESUtil();
		String key = "0e7cef307ba3195eef26fe5c1c9497f5";
		String login = "13563ea8f55256881cf8d142ea7970be763d0960d55f60ca20d3f4de56654bc11ce93ab414310fb887e715687ace2688eZm2L nVfmg5B ZmdwuAhA==";
		String pwd = "Hostire@2022";

		log.info("login décrypté: " + aes.decrypt(key, login));
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

}
