package com.hulkhiretech.payments.utils;

import com.hulkhiretech.payments.paypal.res.error.ErrorDetail;
import com.hulkhiretech.payments.paypal.res.error.PaypalErrorResponse;

public class PaypalOrderUtils {

	private PaypalOrderUtils() {
	    // Private constructor to prevent instantiation
	}
	
	
	public static String getPaypalErrorSummary(PaypalErrorResponse res) {
	    if (res == null) {
	        return "Unknown PayPal error";
	    }

	    StringBuilder summary = new StringBuilder();
	    
	    appendIfPresent(summary, res.getName());
	    appendIfPresent(summary, res.getMessage());
	    appendIfPresent(summary, res.getError());
	    appendIfPresent(summary, res.getErrorDescription());

	    if (res.getDetails() != null && !res.getDetails().isEmpty()) {
	      ErrorDetail detail = res.getDetails().get(0);
	      if (detail != null) {
	        appendIfPresent(summary, detail.getField());
	        appendIfPresent(summary, detail.getIssue());
	        appendIfPresent(summary, detail.getDescription());
	      }
	    }

	    return summary.length() > 0 ? summary.toString() : "Unknown PayPal error";
	}
	
	
	    private static void appendIfPresent(StringBuilder sb, String value) {
	      if (value != null && !value.isBlank()) {
	        if (sb.length() > 0) sb.append(" | ");
	        sb.append(value.trim());
	      }
	    }
	
}
