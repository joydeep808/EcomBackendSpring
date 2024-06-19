package com.oauth.ecom.services.couponCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.ReqRes;
import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.entity.CouponCode;
import com.oauth.ecom.entity.enumentity.DiscountType;
import com.oauth.ecom.repository.CartRepo;
import com.oauth.ecom.repository.CouponCodeRepo;
import com.oauth.ecom.repository.UserRepo;
import com.oauth.ecom.services.redis.RedisService;
import com.oauth.ecom.util.JwtInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CouponCodeService {
    @Autowired
    private CouponCodeRepo couponCodeRepo;
    @Autowired
    private JwtInterceptor jwtInterceptor;
    @Autowired UserRepo userRepo;
    @Autowired CartRepo cartRepo;
    @Autowired RedisService redisService;



  public ReqRes createCoupon(CouponCode couponDto ){
    ReqRes response = new ReqRes();
    
    try {
   CouponCode couponCode =  couponCodeRepo.findByCouponCode(couponDto.getCouponCode()).orElse(null);
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
      Ccode.setMinValue(couponDto.getMinValue());
      Ccode.setDiscountType(couponDto.getDiscountType());
      Ccode.setDiscription(couponDto.getDiscription());
      Ccode.setCategoryApplyed(couponDto.getCategoryApplyed());
      Ccode.setIsPaused(couponDto.getIsPaused());
      Ccode.setCouponCode(couponDto.getCouponCode());
      Ccode.setDiscountUpto(couponDto.getDiscountUpto());
      Ccode.setDiscountPercentage(couponDto.getDiscountUpto());
      CouponCode savedCouponCode = couponCodeRepo.save(Ccode);
      response.sendSuccessResponse(200, "Coupon code saved succcessfully done!"  , savedCouponCode);
      return response;
    } catch (Exception e) {
      response.sendErrorMessage(500, "Server not reachable" , e.getLocalizedMessage());
      return response;
    }
  }
  public ReqRes addCouponInCart(HttpServletRequest request , String couponCode){
    ReqRes response = new ReqRes();
   try {
     String isValidCouponCode =  redisService.getCouponData("couponCode"+couponCode );
    if (isValidCouponCode == null) {
      response.sendErrorMessage(404, "CouponCode not valid");
      return response;
    }
    
    CouponCode foundCouponCode = couponCodeRepo.findByCouponCode(couponCode).orElse(null);
    if (foundCouponCode == null) {
        response.sendSuccessResponse(404, "CouponCode not valid");
        return response;
    }
   long id =  jwtInterceptor.getIdFromJwt(request);
   Cart cart = cartRepo.findByUser(id);
   if (cart == null) {
    response.sendErrorMessage(401, "Unathorized access");
    return response;
   }
   if ( cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
    response.sendErrorMessage(400, "Please add item first");
   }
   if (cart.getCartTotal() < foundCouponCode.getMinValue()) {
    response.sendErrorMessage(400, "Minimum cart value should be"+foundCouponCode.getMinValue());
    return response;
   }
   // checking discount logic
   float DiscountAmount = cart.getCartTotal() / foundCouponCode.getDiscountPercentage();
   if (foundCouponCode.getDiscountType() == DiscountType.FLAT) {
    if (DiscountAmount > foundCouponCode.getDiscountUpto()) cart.setDiscountCartTotal(cart.getCartTotal() -DiscountAmount);
    else cart.setDiscountCartTotal(cart.getCartTotal() -DiscountAmount);
   }
   else{
    if (DiscountAmount > foundCouponCode.getDiscountUpto()) cart.setDiscountCartTotal(cart.getCartTotal() -DiscountAmount);
    else cart.setDiscountCartTotal(cart.getCartTotal() -DiscountAmount);
   }
   cart.setCouponCode(foundCouponCode);
   Cart savedCart = cartRepo.save(cart);
   response.sendSuccessResponse(200, "CouponCode successfully added" , savedCart);
   return response;
   } catch (Exception e) {
    response.sendErrorMessage(500, e.getMessage());
    return response;
   }
  }
  public ReqRes removeCouponCode(HttpServletRequest request){
    ReqRes response = new ReqRes();

    try {
    long id = jwtInterceptor.getIdFromJwt(request);
   Cart cart =  cartRepo.findByUser(id);
   if (cart == null) {
    response.sendErrorMessage(401, "Unauthorized access");
    return response;
   }
   if (cart.getCartItems().isEmpty() || cart.getCartTotal() == 0 || cart.getCouponCode() == null) {
    response.sendErrorMessage(400, "CouponCode not found to remove");
    return response;
   }
    cart.setDiscountCartTotal(cart.getCartTotal());
    cart.setCouponCode(null);      
    Cart savedCart = cartRepo.save(cart);
    response.sendSuccessResponse(200, "CouponCode removed successfully done!" ,savedCart);
    return response;
    } catch (Exception e) {
      response.sendSuccessResponse(200, e.getMessage());
      return response;
    }
  }
}




// public CouponCodeValidationForOrder getCouponCodeForOrder(String couponCode , float netAmount){
//   CouponCodeValidationForOrder codeValidationForOrder = new CouponCodeValidationForOrder();
//  CouponCode foundCouponCode = couponCodeRepo.findByCouponCode(couponCode).orElse(null);
//  if (foundCouponCode == null) {
//   codeValidationForOrder.setDiscountedAmount(0);
//   codeValidationForOrder.setCouponCode(null);
// }
// else{
//  DiscountType type =  foundCouponCode.getDiscountType();
//  if (type == DiscountType.FLAT) {
//     float totalDiscount =  (netAmount / foundCouponCode.getDiscountPercentage());
//     if (totalDiscount > foundCouponCode.getDiscountUpto()) {
//       codeValidationForOrder.setDiscountedAmount(netAmount - foundCouponCode.getDiscountUpto());
//     }
//     else  codeValidationForOrder.setDiscountedAmount(netAmount - totalDiscount);
    
//  }
//  else{
//   float totalDiscount = (netAmount / 100 * foundCouponCode.getDiscountPercentage());
//   if (totalDiscount > foundCouponCode.getDiscountUpto()) {
//   codeValidationForOrder.setDiscountedAmount(netAmount - foundCouponCode.getDiscountUpto());
    
//   }
//  else  codeValidationForOrder.setDiscountedAmount(netAmount - totalDiscount);
//  }
// }
// foundCouponCode.setStock(foundCouponCode.getStock() - 1);
// couponCodeRepo.save(foundCouponCode);
// return codeValidationForOrder;
// }

// // 