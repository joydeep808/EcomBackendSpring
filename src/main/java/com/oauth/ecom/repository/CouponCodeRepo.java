package com.oauth.ecom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oauth.ecom.entity.CouponCode;
@Repository
public interface CouponCodeRepo extends JpaRepository<CouponCode , Object>{
  
  Optional<CouponCode> findByCouponCode(String couponCode);

}
