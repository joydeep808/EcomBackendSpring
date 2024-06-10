package com.oauth.jwtauth.repository;
import org.springframework.stereotype.Repository;
import com.oauth.jwtauth.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface OrderRepo extends JpaRepository<Order , Long>{
  
}
