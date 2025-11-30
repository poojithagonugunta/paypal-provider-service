package com.hulkhiretech.payments.hhtp;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Data;

@Data
public class HttpRequest {

	
	private HttpMethod hhtpMethod;
	
	private String url;
	
	private HttpHeaders hhtpHeaders;
	
	private Object body;
	
}
