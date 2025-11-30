package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.CreateOrderResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
public class PaymentController {

	private final PaymentService paymentService;
	
	@PostMapping
	public CreateOrderResponse createOrder(@RequestBody CreateOrderReq createOrderReq) {
		log.info("Creating order in paypal provider service");
		CreateOrderResponse response=paymentService.createOrder(createOrderReq);
		log.info("Order creation response from service: {} ",response);
		return response;
	} 
	
	@PostMapping("/{orderId}/capture")
	public CreateOrderResponse captureOrder(@PathVariable String orderId) {
		log.info("Capturing order in paypal provider service with orderId: {} ",orderId);
		return paymentService.captureOrder(orderId);
	}
	@PostConstruct
	public void init() {
		log.info("PaymentController Initialized "+"paymentServiceImpl{}",paymentService);
	}
}
