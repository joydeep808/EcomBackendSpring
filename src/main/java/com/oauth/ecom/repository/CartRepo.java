package com.oauth.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.ecom.entity.Cart;
@Repository
public interface CartRepo extends JpaRepository<Cart , Object> {
  @Query(value = "SELECT * FROM cart WHERE user_id =:user", nativeQuery = true)
  Cart findByUser(@Param("user") long user);
  @Transactional
  @Query(value = "SELECT c.*, u.*, ci.*, p.* FROM cart AS c LEFT JOIN users AS u ON u.id = c.user_id LEFT JOIN couponcode AS cc ON (cc.id  = c.coupon_code_id OR c.coupon_code_id IS NULL)  LEFT JOIN cart_items AS ci ON ci.cart_id= c.id LEFT JOIN products AS p ON p.id  = ci.product_id WHERE c.user_id =:userId", nativeQuery = true)
  List<Object> findByUserId(@Param("userId") long userId);

  // Cart findByUser(UserEntity userEntity);
}
