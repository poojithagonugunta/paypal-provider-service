**Overview**

The PayPal Provider Service is a microservice responsible for handling online payments using PayPal Checkout APIs.
It exposes REST endpoints to:

1.Create a PayPal order
2.Redirect the user to PayPal Checkout
3.Capture the payment after approval
4.Return final payment status to the client application

This service acts as a middleware between your application (UI / another backend service) and PayPal REST APIs and Implemented Caching in order to store the token and improved the performance.

**Tech Stack**

Java 17 / Spring Boot
Spring Web / Rest Client
PayPal REST APIs (v2/checkout/orders)
Maven
JSON (Jackson ObjectMapper)
GlobalExceptionhandler
Redis Caching

**1. Create Order Flow
**
1.Client clicks “Checkout”.
2.Client sends /create-order request to the service.
3.Service calls
POST https://api-m.paypal.com/v2/checkout/orders

PayPal returns:
Order ID
Approval URL

Service returns approval URL to client.

Client redirects user to PayPal gateway.

**2. Capture Order Flow**

1.User completes approval on PayPal UI.
2.PayPal redirects back with token (orderId).
3.Client calls /capture-order?orderId=<orderId>.
4.Service calls
POST https://api-m.paypal.com/v2/checkout/orders/{orderId}/capture
PayPal returns:
Status = COMPLETED / FAILED
Service returns final receipt to UI.

**Service-Level Responsibilities: 
✔ Generate PayPal OAuth Token**

Before any API call, service calls
https://api-m.paypal.com/v1/oauth2/token
using clientId + secret.
**✔ Create PayPal Order**
**Builds CreateOrderReq object containing:**
Amount, Currency
Intent = CAPTURE
Return & Cancel URLs
**✔ Capture Payment**
After approval, service confirms payment with PayPal.
✔ Map PayPal JSON → Local POJOs
Using ObjectMapper.

**✔ Error Handling****
Token failure
Order creation failure
Capture failure
Network exceptions
Invalid payloads
