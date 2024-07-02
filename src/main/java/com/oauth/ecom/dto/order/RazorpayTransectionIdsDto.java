package com.oauth.ecom.dto.order;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oauth.ecom.entity.enumentity.PaymentType;

import lombok.Data;

@Data
public class RazorpayTransectionIdsDto {
  private String razorpay_order_id;
  private long user ;
  private PaymentType paymentType;
  public RazorpayTransectionIdsDto (JSONObject object){
    this.razorpay_order_id = object.getString("razorpay_order_id");
    this.user = object.getLong("user");
    this.paymentType = object.getEnum(PaymentType.class , "paymentType");
  }


  @JsonCreator
    public RazorpayTransectionIdsDto(
            @JsonProperty("razorpay_order_id") String razorpay_order_id,
            @JsonProperty("user") long user,
            @JsonProperty("paymentType") PaymentType paymentType
            
            ) {
        this.razorpay_order_id = razorpay_order_id;
        this.paymentType = paymentType;
        this.user = user;
    }
}
