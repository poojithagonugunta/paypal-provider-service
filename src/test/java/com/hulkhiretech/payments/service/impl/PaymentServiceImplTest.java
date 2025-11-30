package com.hulkhiretech.payments.service.impl;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.hhtp.HttpRequest;
import com.hulkhiretech.payments.hhtp.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.CreateOrderResponse;
import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.service.ValidateCaptureOrder;
import com.hulkhiretech.payments.service.ValidateCreateOrder;
import com.hulkhiretech.payments.service.helper.CaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.CreateOrderHelper;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private HttpServiceEngine httpServiceEngine;

    @Mock
    private CreateOrderHelper createOrderHelper;

    @Mock
    private ValidateCreateOrder validateCreateOrder;

    @Mock
    private ValidateCaptureOrder validateCaptureOrder;

    @Mock
    private CaptureOrderHelper captureOrderHelper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    // ============================================================
    // TEST: createOrder() → SUCCESS
    // ============================================================
    @Test
    void testCreateOrderSuccess() {

        CreateOrderReq req = new CreateOrderReq();

        // Step 1: Validation
        doNothing().when(validateCreateOrder).vadilateCreateOrderRequest(req);

        // Step 2: Token
        when(tokenService.getAccessToken()).thenReturn("ACCESS_TOKEN");

        // Step 3: Prepare HttpRequest
        HttpRequest httpRequest = new HttpRequest();
        when(createOrderHelper.prepareCreateHttpRequest(req, "ACCESS_TOKEN"))
                .thenReturn(httpRequest);

        // Step 4: Mock HTTP response
        ResponseEntity<String> mockResponse =
                ResponseEntity.ok("{\"id\":\"123\",\"status\":\"CREATED\"}");

        when(httpServiceEngine.makeHttpCall(httpRequest)).thenReturn(mockResponse);

        // Step 5: Convert API response to Java Object
        CreateOrderResponse finalResponse = new CreateOrderResponse();
        finalResponse.setOrderId("123");
        finalResponse.setOrderStatus("CREATED");

        when(createOrderHelper.handlePaypalResponse(mockResponse))
                .thenReturn(finalResponse);

        // ===== Call service =====
        CreateOrderResponse response = paymentService.createOrder(req);

        // ===== Assertions =====
        assertNotNull(response);
        assertEquals("123", response.getOrderId());
        assertEquals("CREATED", response.getOrderStatus());

        // ===== Verifications =====
        verify(validateCreateOrder, times(1)).vadilateCreateOrderRequest(req);
        verify(tokenService, times(1)).getAccessToken();
        verify(createOrderHelper, times(1)).prepareCreateHttpRequest(req, "ACCESS_TOKEN");
        verify(httpServiceEngine, times(1)).makeHttpCall(httpRequest);
        verify(createOrderHelper, times(1)).handlePaypalResponse(mockResponse);
    }


    // ============================================================
    // TEST: captureOrder() → SUCCESS
    // ============================================================
    @Test
    void testCaptureOrderSuccess() {

        String orderId = "ORDER123";

        // Step 1: Validate
        doNothing().when(validateCaptureOrder).vadilateCaptureOrderRequest(orderId);

        // Step 2: Token
        when(tokenService.getAccessToken()).thenReturn("ACCESS_TOKEN");

        // Step 3: Prepare HttpRequest
        HttpRequest request = new HttpRequest();
        when(captureOrderHelper.prepareCaptureHttpRequest(orderId, "ACCESS_TOKEN"))
                .thenReturn(request);

        // Step 4: Mock HTTP call
        ResponseEntity<String> mockResponse =
                ResponseEntity.ok("{\"id\":\"ORDER123\",\"status\":\"COMPLETED\"}");
        when(httpServiceEngine.makeHttpCall(request)).thenReturn(mockResponse);

        // Step 5: Convert response
        CreateOrderResponse finalResponse = new CreateOrderResponse();
        finalResponse.setOrderId("ORDER123");
        finalResponse.setOrderStatus("COMPLETED");

        when(captureOrderHelper.createCaptureOrderResponse(mockResponse, orderId))
                .thenReturn(finalResponse);

        // ===== Call service =====
        CreateOrderResponse response = paymentService.captureOrder(orderId);

        // ===== Assertions =====
        assertEquals("ORDER123", response.getOrderId());
        assertEquals("COMPLETED", response.getOrderStatus());

        // ===== Verifications =====
        verify(validateCaptureOrder, times(1)).vadilateCaptureOrderRequest(orderId);
        verify(tokenService, times(1)).getAccessToken();
        verify(captureOrderHelper, times(1)).prepareCaptureHttpRequest(orderId, "ACCESS_TOKEN");
        verify(httpServiceEngine, times(1)).makeHttpCall(request);
        verify(captureOrderHelper, times(1)).createCaptureOrderResponse(mockResponse, orderId);
    }

}
