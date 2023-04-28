package com.gulfcam.fuelcoupon.authentication.service;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Component
@Slf4j
@Getter
@Setter
public class JwtUtils {

	@Value("${jwt.uri}")
	private String uri;

	@Value("${jwt.header}")
	private String header;

	@Value("${jwt.prefix}")
	private String prefix;

	@Value("${jwt.expirationBearerTokenInMs}")
	private int expirationBearerToken;
	
	@Value("${jwt.expirationEmailVerifTokenInMs}")
	private int expirationEmailVerifToken;

	@Value("${jwt.expirationRefreshTokenInMs}")
	private int expirationRefreshToken;
	
	@Value("${jwt.expirationEmailVerifResetPasswordInMs}")
	private int expirationEmailResetPassword;

	@Value("${jwt.secretBearerToken}")
	private String secretBearerToken;
	
	@Value("${jwt.secretRefreshToken}")
	private String secretRefreshToken;

	private static final String AUTHENTICATED = "authenticated";
	public static final long TEMP_TOKEN_VALIDITY_IN_MILLIS = 300000;


	public String generateJwtToken(String username, int expiration, String secret,boolean authenticated ) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + (authenticated ? expiration : TEMP_TOKEN_VALIDITY_IN_MILLIS));
		return Jwts.builder()
				.setSubject(username)
				.claim(AUTHENTICATED, authenticated)
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	} 
	
	public int generateOtpCode() {
		return  (1000 + new Random().nextInt(9000));
	}

	public String getIdGulfcamFromJwtToken(String token, String secret) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}


	public boolean validateJwtToken(String token, String secret) throws Exception {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
			return true;
		} catch (SignatureException|MalformedJwtException|ExpiredJwtException|UnsupportedJwtException|IllegalArgumentException e) {
			throw e;
		} 
	}


	public Boolean isAuthenticated(String token) {

		Claims claims = Jwts.parser().setSigningKey(secretRefreshToken).parseClaimsJws(token).getBody();
		return claims.get(AUTHENTICATED, Boolean.class);
	}

	public String parseJwt(HttpServletRequest request) {
		String prefixAndToken = request.getHeader(header);
		if (prefixAndToken != null) {
			String tokenOpt = parseJwt(prefixAndToken);
			return tokenOpt;
		}
		return null;
	}

	public String parseJwt(String bearerToken) {
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(prefix)) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	public Long generateInternalReference() {
		Long internalReference =  Long.parseLong((1000 + new Random().nextInt(9000)) + RandomStringUtils.random(5, 40, 150, false, true, null, new SecureRandom()));
		return internalReference;
	}

	public String refreshToken(String token) throws Exception {
		String username = getIdGulfcamFromJwtToken(token, secretRefreshToken);
		if (username.isEmpty()) {
			throw new AuthorizationServiceException("Invalid token claims");
		}
		return generateJwtToken(username, expirationBearerToken, secretBearerToken,true);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
