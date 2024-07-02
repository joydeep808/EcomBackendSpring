package com.oauth.ecom.dto.order.payment;

import com.razorpay.Payment;

import lombok.Data;

@Data
public class RazorpayPaymentDTO {
  private String id;
  private String orderId;
  private String status;
  private int amount;
  private String method;
  // Add other fields you need

  // Constructors, getters, and setters
  public RazorpayPaymentDTO(Payment payment) {
      this.id = payment.get("id");
      this.orderId = payment.get("order_id");
      this.status = payment.get("status");
      this.amount = payment.get("amount");
      this.method = payment.get("method");
      }
}

// {"notes":{"address":"Razorpay Corporate Office"},"fee":91212,"description":"Test Transaction","created_at":1719482163,"amount_refunded":3864900,"bank":"ICIC","error_reason":null,"error_description":null,"acquirer_data":{"bank_transaction_id":"2629366"},"captured":true,"contact":"+919000090000","invoice_id":null,"currency":"INR","id":"pay_ORk8x1dUcxIAmr","international":false,"email":"gaurav.kumar@example.com","amount":3864900,"refund_status":"full","wallet":null,"method":"netbanking","vpa":null,"error_source":null,"error_step":null,"tax":13914,"card_id":null,"error_code":null,"order_id":"order_ORk8kQFA89LJYi","entity":"payment","status":"refunded"}