package com.oauth.jwtauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oauth.jwtauth.entity.CouponCode;
@Repository
public interface CouponCodeRepo extends JpaRepository<CouponCode , Object>{
  
  CouponCode findByCouponCode(String couponCode);
  
}
