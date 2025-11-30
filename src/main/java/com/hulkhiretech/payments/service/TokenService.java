package com.hulkhiretech.payments.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.hhtp.HttpRequest;
import com.hulkhiretech.payments.hhtp.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.res.PaypalOAuthToken;
import com.hulkhiretech.payments.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
	
	private final RedisService redisService;


	private final HttpServiceEngine httpServiceEngine;

	
	
	private final ObjectMapper objectMapper;
	
	private final JsonUtils jsonUtils;
	
	@Value("${paypal.client.id}")
	private String clientId;
	
	@Value("${paypal.client.secret}")
	private String clientSecret;
	
	@Value("${paypal.oauth.url}")
	private String oauthUrl;

	public String getAccessToken() {
		log.info("retreving access token from token service");
		
		String accessToken=redisService.getValue(Constant.PAYPAL_ACCESS_TOKEN_KEY);

		if(accessToken!=null) {
			log.info("Found cached accesstoken : {}",accessToken);
         
            return accessToken;
		}

		log.info("No cached accesstoken Found calling Outh service");
		

		HttpHeaders headers = new HttpHeaders();

		
		headers.setBasicAuth(clientId,clientSecret);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);



		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);


		HttpRequest hhtpRequest=new HttpRequest();
		hhtpRequest.setHhtpMethod(HttpMethod.POST);
		
		hhtpRequest.setUrl(oauthUrl);
		hhtpRequest.setHhtpHeaders(headers);
		hhtpRequest.setBody(formData);

		log.info("Prepared hhtpRequest for Oauth call is : {}",hhtpRequest);
		ResponseEntity<String> response=httpServiceEngine.makeHttpCall(hhtpRequest);
		String tokenBody=response.getBody();
		log.info("the token response retured is:",response);
		
		log.info("retrived the token {}",tokenBody);
		
		PaypalOAuthToken paypalOAuthToken= jsonUtils.fromJson(tokenBody,PaypalOAuthToken.class);
		
		accessToken=paypalOAuthToken.getAccessToken();
		
		redisService.setExprirykeyvalue(Constant.PAYPAL_ACCESS_TOKEN_KEY, accessToken,paypalOAuthToken.getExpiresIn()-300);
		
		
		return accessToken;
		

	}



}
