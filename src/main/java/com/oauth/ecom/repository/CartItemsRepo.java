package com.oauth.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.ecom.entity.CartItems;
import com.oauth.ecom.mappers.cart.CartItemsMapper;
import com.oauth.ecom.mappers.cart.FindCartItemsForCheckout;

@Repository
public interface CartItemsRepo  extends JpaRepository<CartItems , Long>{

  // CartItems findByProductAndUser(Long );
  @Transactional
  @Modifying
  @Query(value = "delete from cart_items where cart_id =:cart" , nativeQuery = true)
  void deleteCartItems(@Param("cart") Long cart);

  @Query(value = "SELECT * FROM cart_items WHERE cart_id =:id", nativeQuery = true)
  List<CartItems>  findByCartId(@Param("id") Long id);

  @Query(value = "SELECT p.id as product_id , p.price as product_price ,  p.stock as product_quantity , c.quantity , c.id FROM cart_items as c LEFT JOIN products as p ON p.id = c.product_id WHERE c.cart_id = :id", nativeQuery = true)
  List<CartItemsMapper> findCartItemsProducts(@Param("id") Long id);


  @Query(value = "SELECT p.id as product_id , p.price as product_price  ,p.color, c.quantity , c.id FROM cart_items as c LEFT JOIN products as p ON p.id = c.product_id WHERE c.cart_id = :id", nativeQuery = true)
  List<FindCartItemsForCheckout> findCartItemsForCheckout(@Param("id") Long id);


  @Query(value = "SELECT * FROM cart_items WHERE cart_id = :cartId AND product_id = :productId", nativeQuery = true)
  CartItems findByCartIdAndProduct(@Param("cartId") Long cartId ,@Param("productId") Long productId);


}
