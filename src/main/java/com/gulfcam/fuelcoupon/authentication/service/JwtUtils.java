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
import java.time.LocalDate;
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

	public String geerateIdTransaction() {
				return "IMEM" +"-"+ LocalDate.now().toString().replace("-","")  +"-"+ RandomStringUtils.random(4, 35, 125, true, true, null, new SecureRandom()) +"-"+ (100 + new Random().nextInt(900)) +"-"+ RandomStringUtils.random(4, 35, 125, true, true, null, new SecureRandom());
	}

	public String gerateReferenceOfferJob(String idjobetrouveOrganization,String offerTitle) {
		String offerConcat = offerTitle.replaceAll("[^A-Za-z0-9]","").substring(0,3).toUpperCase();
				return "JOB" +"-"+ LocalDate.now().getYear()+"-"+LocalDate.now().getMonthValue()+LocalDate.now().getDayOfMonth()+"-"+ idjobetrouveOrganization+"-"+offerConcat+"-"+ RandomStringUtils.random(5, 35, 125, true, true, null, new SecureRandom()) ;
	}

	public String generateIdJobEtrouve(String code_country) {
      String jobetrouveId =  code_country +"-" + (100 + new Random().nextInt(900)) +"-"+ RandomStringUtils.random(4, 35, 125, true, true, null, new SecureRandom());
		return    jobetrouveId.toUpperCase();
	}
	public String generateIdJobEtrouveEntreprise(String code_country,String orgname) {
		String orgConcat = orgname.replaceAll("[^A-Za-z0-9]","").substring(0,3);
      return    orgConcat.toUpperCase() +"-"+  code_country +"-" + (100 + new Random().nextInt(900)) +"-"+ RandomStringUtils.random(4, 35, 125, true, true, null, new SecureRandom());
	}
	
	public String getIdJobEtrouveFromJwtToken(String token, String secret) {
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

	public Long getIdJobEtrouveFromToken(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(secretRefreshToken)
				.parseClaimsJws(token)
				.getBody();

		return Long.parseLong(claims.getSubject());
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

	public String refreshToken(String token) throws Exception {
		String username = getIdJobEtrouveFromJwtToken(token, secretRefreshToken);
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
