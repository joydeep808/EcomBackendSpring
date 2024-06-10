package com.oauth.jwtauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oauth.jwtauth.entity.OrderItems;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems , Long>{

}
