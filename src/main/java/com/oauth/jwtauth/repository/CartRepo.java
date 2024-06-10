package com.oauth.jwtauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oauth.jwtauth.entity.Cart;
@Repository
public interface CartRepo extends JpaRepository<Cart , Object> {
  @Query(value = "SELECT * FROM cart WHERE user_id =:user", nativeQuery = true)
  Cart findByUser(@Param("user") Long user);
}
