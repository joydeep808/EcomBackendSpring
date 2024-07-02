package com.oauth.ecom.dto.order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface OrderDetailsSendDTO {
  Optional<String> getStatus();
    Optional<Long> getId();
    Optional<LocalDateTime> getExpectedDeliveryDate();
    Optional<Double> getTotalAmount();
    Optional<Double> getDiscountAmount();
    Optional<Double> getNetAmount();
    Optional<String> getTrackingId();
    Optional<LocalDateTime> getCreatedAt();
    Optional<String> getCouponCode();
    Optional<Integer> getQuantity();
    Optional<Double> getTotalPrice();
    Optional<String> getColor();
    Optional<String> getName();
    Optional<List<String>> getImages();
    Optional<Integer> getPinCode();
    Optional<Long> getPhone();
    Optional<String> getFullAddress();
    Optional<String> getState();
}
