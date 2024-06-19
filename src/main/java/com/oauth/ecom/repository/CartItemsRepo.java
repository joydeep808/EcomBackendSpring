package com.oauth.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.ecom.entity.CartItems;

@Repository
public interface CartItemsRepo  extends JpaRepository<CartItems , Long>{

  // CartItems findByProductAndUser(Long );
  @Transactional
  @Modifying
  @Query(value = "delete from cart_items where cart_id =:cart" , nativeQuery = true)
  void deleteCartItems(@Param("cart") Long cart);
}
