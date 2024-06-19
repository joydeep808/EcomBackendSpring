package com.oauth.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oauth.ecom.entity.OrderItems;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems , Long>{

}
