package com.oauth.ecom.repository;
import org.springframework.stereotype.Repository;

import com.oauth.ecom.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface OrderRepo extends JpaRepository<Order , Long>{
  
}
