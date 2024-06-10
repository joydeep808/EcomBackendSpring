package com.oauth.jwtauth.services.couponCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.couponcode.CouponDto;
import com.oauth.jwtauth.entity.CouponCode;
import com.oauth.jwtauth.repository.CouponCodeRepo;

@Service
public class CouponCodeService {
    @Autowired
    private CouponCodeRepo couponCodeRepo;

  public ReqRes createCoupon(CouponDto couponDto ){
    ReqRes response = new ReqRes();
    try {
   CouponCode couponCode =  couponCodeRepo.findByCouponCode(couponDto.getCouponCode());
      if (couponCode != null ) {
        response.setMessage("Coupon code already exist");
        response.setStatusCode(400);
        return response;
      }
      CouponCode Ccode = new CouponCode(); 
      Ccode.setCouponCode(couponDto.getCouponCode());
      Ccode.setStartDate(couponDto.getStartDate());
      Ccode.setEndDate(couponDto.getEndDate());
      Ccode.setStock(couponDto.getStock());
      Ccode.setDiscountType(couponDto.getDiscountType());
      Ccode.setDiscription(couponDto.getDiscription());
      Ccode.setCategoryApplyed(couponDto.getCategoryApplyed());
      Ccode.setCategoryApplyed(couponDto.getCategoryApplyed());
      Ccode.setIsPaused(couponDto.getIsPaused());
      Ccode.setCouponCode(couponDto.getCouponCode());
      Ccode.setDiscountUpto(couponDto.getDiscountUpto());
      CouponCode savedCouponCode = couponCodeRepo.save(Ccode);
      response.setStatusCode(200);
      response.setMessage("Coupon Code saved successfully done!");
      response.setIsSuccess(true);
      response.setData(savedCouponCode);
      return response;
    } catch (Exception e) {
      response.setMessage("Server not reachable");
      response.setError(e.getLocalizedMessage());
      response.setStatusCode(500);
      return response;
    }
  }
}
