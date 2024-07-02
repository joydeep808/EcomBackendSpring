package com.oauth.ecom.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oauth.ecom.dto.order.OrderDetailsSendDTO;
import com.oauth.ecom.entity.Order;
@Repository
public interface OrderRepo extends JpaRepository<Order , Long>{
  @Query(value = 
  "SELECT o.status, o.id, o.expected_delevery_date,o.total_amount, o.discount_amount, o.net_amount, o.tracking_id, o.created_at," +
  "c.coupon_code , "+
  "oi.quantity , oi.totalprice,oi.color , p.name ,p.images ,"+
  "a.pincode , a.phone , a.full_address , a.state "+
  "FROM orders AS o "+
  "LEFT JOIN users AS u ON u.id = o.user_id "+
  "LEFT JOIN couponcode AS c ON c.id = o.coupon_code_id "+
  "LEFT JOIN address AS a ON a.id = o.address_id "+
  "LEFT JOIN order_items AS oi ON oi.order_id = o.id  "+
  "LEFT JOIN products AS p ON p.id = oi.product "+
  "WHERE o.user_id = :user" , nativeQuery = true)
  List<OrderDetailsSendDTO> findOrders(@Param("user") long user);
  @Query(value = "SELECT o.*" +
        "FROM orders AS o" +
        "WHERE o.created_at > (NOW() AT TIME ZONE 'Asia/Kolkata') - INTERVAL '20hours';" , nativeQuery = true)
  Page<Order> findPendingOrders(Pageable pageable);

  @Query(value = "select o.* from orders as o  where o.status = 'PENDING' AND o.transection_id=:id" , nativeQuery = true)
  Optional<Order> findByTransectionId(@Param("id") String id);

  @Query(value = "select o.* from orders as o where o.created_at < (NOW() AT TIME ZONE 'Asia/Koktakta') - INTERVAL '24hours';" , nativeQuery = true)
  List<Order> findExpiredOrders(Pageable pageable);
  
}
