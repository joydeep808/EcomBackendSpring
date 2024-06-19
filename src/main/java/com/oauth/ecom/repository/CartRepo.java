package com.oauth.ecom.repository;

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
  @Query(value = "select * from cart where user_id =:userId", nativeQuery = true)
  Cart findByUserId(@Param("userId") long userId);

  // Cart findByUser(UserEntity userEntity);
}
