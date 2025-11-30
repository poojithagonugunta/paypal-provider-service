package com.hulkhiretech.payments.hhtp;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

	private final RestClient restClient;

	public ResponseEntity<String> makeHttpCall(HttpRequest hhtpRequest) {
		log.info("Making hhtp call in httpservice Engine");


		try {

			ResponseEntity<String> response=restClient.method(hhtpRequest.getHhtpMethod()).
					uri(hhtpRequest.getUrl()).
					headers(restCilentHeader->restCilentHeader.addAll(hhtpRequest.getHhtpHeaders())).
					body(hhtpRequest.getBody()).
					retrieve().toEntity(String.class);
       log.info("Response from http call: {}", response);

			return response;
		}

		catch (HttpClientErrorException | HttpServerErrorException e) {
			// valid error response from server
			log.error("HTTP error response received: {}", e.getMessage(), e);

			if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT ||
					e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
				log.error("Service is unavailable or gateway timed out");
				throw new PaypalProviderException(ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getCode(),
						ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getMessage(),
						HttpStatus.SERVICE_UNAVAILABLE);
			}


			// return ResponseEntity with error details
			String errorResponse = e.getResponseBodyAsString();
			log.info("Error response body: {}", errorResponse);

			return ResponseEntity
					.status(e.getStatusCode())
					.body(errorResponse);

		}
		catch (Exception e) { // No Response case.
			log.error("Exception while preparing form data: {}", e.getMessage(), e);

			throw new PaypalProviderException(
					ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getCode(),
					ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getMessage(),
					HttpStatus.SERVICE_UNAVAILABLE);
		}

	}


}
