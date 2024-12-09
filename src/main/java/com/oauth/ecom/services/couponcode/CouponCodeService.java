package com.oauth.ecom.services.couponcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.oauth.ecom.entity.*;
import com.oauth.ecom.entity.enumentity.DiscountType;
import com.oauth.ecom.repository.*;
import com.oauth.ecom.services.redis.RedisService;
import com.oauth.ecom.util.*;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CouponCodeService {
  @Autowired
  private CouponCodeRepo couponCodeRepo;
  @Autowired
  private JwtInterceptor jwtInterceptor;
  @Autowired
  UserRepo userRepo;
  @Autowired
  CartRepo cartRepo;
  @Autowired
  RedisService redisService;

 
  public ResponseEntity<ReqRes<CouponCode>> createCoupon(CouponCode couponDto) {
    ReqRes<CouponCode> response = new ReqRes<>();

    /**
     * Check if the coupon code already exists.
     */
    CouponCode couponCode = couponCodeRepo.findByCouponCode(couponDto.getCouponCode()).orElse(null);
    if (couponCode != null) {
      /**
       * If the coupon code already exists, return an error message.
       */
      return response.sendErrorMessage(400, "Coupon code already exist").sendResponseEntity();
    }

    /**
     * Create a new coupon code with the information provided.
     */
    CouponCode code = CouponCode.builder().couponCode(couponDto.getCouponCode())
        .category(couponDto.getCategory())
        .discountType(couponDto.getDiscountType())
        .discription(couponDto.getDiscription())
        .discountUpto(couponDto.getDiscountUpto())
        .discountPercentage(couponDto.getDiscountPercentage())
        .minValue(couponDto.getMinValue())
        .startDate(couponDto.getStartDate())
        .endDate(couponDto.getEndDate())
        .stock(couponDto.getStock())
        .isPaused(couponDto.getIsPaused())
        .categoryApplyed(couponDto.getCategoryApplyed())
        .discription(couponDto.getDiscription())
        .build();

    /**
     * Save the coupon code in the database.
     */
    CouponCode savedCouponCode = couponCodeRepo.save(code);

    /**
     * Return a response entity with the saved coupon code.
     */
    return response.sendSuccessResponse(200, "Coupon code saved succcessfully done!", savedCouponCode).sendResponseEntity();
  }


 
  public ResponseEntity<ReqRes<Cart>> addCouponInCart(HttpServletRequest request, String couponCode) throws Exception {
    ReqRes<Cart> response = new ReqRes<>();

    /**
     * Check if the coupon code is valid by querying the redis database.
     */
    String isValidCouponCode = redisService.getCouponData("couponCode" + couponCode);
    if (isValidCouponCode == null) {
      return response.sendErrorMessage(404, "CouponCode not valid").sendResponseEntity();
      
    }

    /**
     * Check if the coupon code exists in the database.
     */
    CouponCode foundCouponCode = couponCodeRepo.findByCouponCode(couponCode).orElse(null);
    if (foundCouponCode == null) {
      return response.sendSuccessResponse(404, "CouponCode not valid").sendResponseEntity();
      
    }

    /**
     * Get the user's id from the request.
     */
    long id = jwtInterceptor.getIdFromJwt(request);

    /**
     * Get the user's cart from the database.
     */
    Cart cart = cartRepo.findByUser(id);
    if (cart == null) {
      return response.sendErrorMessage(401, "Unathorized access").sendResponseEntity();
      
    }

    /**
     * Check if the cart is empty.
     */
    if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
      return response.sendErrorMessage(400, "Please add item first").sendResponseEntity();
    }

    /**
     * Check if the cart's total value is less than the minimum value required for the coupon code.
     */
    if (cart.getCartTotal() < foundCouponCode.getMinValue()) {
      return response.sendErrorMessage(400, "Minimum cart value should be" + foundCouponCode.getMinValue()).sendResponseEntity();
      
    }

    /**
     * Apply the discount to the cart.
     * 
     * If the discount type is flat, calculate the discount amount as the total value of the cart divided by the discount percentage.
     * If the discount amount is greater than the discount limit, set the discount amount to the discount limit.
     * 
     * If the discount type is percentage, calculate the discount amount as the total value of the cart divided by 100 times the discount percentage.
     * If the discount amount is greater than the discount limit, set the discount amount to the discount limit.
     * 
     * Subtract the discount amount from the cart's total value and set the result as the cart's discounted total value.
     * 
     * Set the coupon code in the cart.
     */
    if (foundCouponCode.getDiscountType() == DiscountType.FLAT) {
      float DiscountAmount = cart.getCartTotal() / foundCouponCode.getDiscountPercentage();
      System.out.println(DiscountAmount);
      if (DiscountAmount > foundCouponCode.getDiscountUpto())
        cart.setDiscountCartTotal(cart.getCartTotal() - foundCouponCode.getDiscountUpto());
      else
        cart.setDiscountCartTotal(cart.getCartTotal() - DiscountAmount);
    } else {
      float DiscountAmount = cart.getCartTotal() / 100 * foundCouponCode.getDiscountPercentage();
      if (DiscountAmount > foundCouponCode.getDiscountUpto())
        cart.setDiscountCartTotal(cart.getCartTotal() - foundCouponCode.getDiscountUpto());
      else
        cart.setDiscountCartTotal(cart.getCartTotal() - DiscountAmount);
    }
    cart.setCouponCode(foundCouponCode);
    Cart savedCart = cartRepo.save(cart);
    return response.sendSuccessResponse(200, "CouponCode successfully added", savedCart).sendResponseEntity();
  }

  /**
   * Removes a coupon code from the user's cart.
   * 
   * This method expects the user to be authenticated.
   * 
   * It first checks if the user's cart exists and if it is not empty.
   * 
   * If the cart is empty, it returns a 400 error.
   * 
   * If the cart is not empty, it removes the coupon code from the cart, sets the discounted total to the original total and saves the cart.
   * 
   * @param request the HTTP request
   * @return a response entity with the updated cart
   * @throws Exception if an exception occurs
   */
  public ResponseEntity<ReqRes<Cart>> removeCouponCode(HttpServletRequest request) throws Exception {
    ReqRes<Cart> response = new ReqRes<>();

    // Extract the user's ID from the request.
    long id = jwtInterceptor.getIdFromJwt(request);

    // Find the user's cart.
    Cart cart = cartRepo.findByUser(id);

    // If the cart doesn't exist, return a 401 error.
    if (cart == null) {
      return response.sendErrorMessage(401, "Unauthorized access").sendResponseEntity();
    }

    // If the cart is empty, return a 400 error.
    if (cart.getCartItems().isEmpty() || cart.getCartTotal() == 0 || cart.getCouponCode() == null) {
      return response.sendErrorMessage(400, "CouponCode not found to remove").sendResponseEntity();
    }

    // Remove the coupon code from the cart.
    cart.setDiscountCartTotal(cart.getCartTotal());
    cart.setCouponCode(null);

    // Save the updated cart.
    Cart savedCart = cartRepo.save(cart);

    // Return a response entity with the updated cart.
    return response.sendSuccessResponse(200, "CouponCode removed successfully done!", savedCart).sendResponseEntity();
  }

}
// public CouponCodeValidationForOrder getCouponCodeForOrder(String couponCode ,
// float netAmount){
// CouponCodeValidationForOrder codeValidationForOrder = new
// CouponCodeValidationForOrder();
// CouponCode foundCouponCode =
// couponCodeRepo.findByCouponCode(couponCode).orElse(null);
// if (foundCouponCode == null) {
// codeValidationForOrder.setDiscountedAmount(0);
// codeValidationForOrder.setCouponCode(null);
// }
// else{
// DiscountType type = foundCouponCode.getDiscountType();
// if (type == DiscountType.FLAT) {
// float totalDiscount = (netAmount / foundCouponCode.getDiscountPercentage());
// if (totalDiscount > foundCouponCode.getDiscountUpto()) {
// codeValidationForOrder.setDiscountedAmount(netAmount -
// foundCouponCode.getDiscountUpto());
// }
// else codeValidationForOrder.setDiscountedAmount(netAmount - totalDiscount);

// }
// else{
// float totalDiscount = (netAmount / 100 *
// foundCouponCode.getDiscountPercentage());
// if (totalDiscount > foundCouponCode.getDiscountUpto()) {
// codeValidationForOrder.setDiscountedAmount(netAmount -
// foundCouponCode.getDiscountUpto());

// }
// else codeValidationForOrder.setDiscountedAmount(netAmount - totalDiscount);
// }
// }
// foundCouponCode.setStock(foundCouponCode.getStock() - 1);
// couponCodeRepo.save(foundCouponCode);
// return codeValidationForOrder;
// }

// //