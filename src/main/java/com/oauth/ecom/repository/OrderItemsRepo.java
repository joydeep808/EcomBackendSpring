package com.oauth.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.oauth.ecom.entity.OrderItems;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems , Long>{
  @Query(value = "select oi.* from order_items as oi where oi.order_id =:orderId" , nativeQuery = true)
  List<OrderItems> findByOrderId(@Param("orderId") long orderId);
  
}
