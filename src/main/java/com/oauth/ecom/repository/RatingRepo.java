package com.oauth.ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oauth.ecom.dto.rating.RatingDto;
import com.oauth.ecom.entity.Rating;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface RatingRepo extends JpaRepository<Rating ,Long >{
  
  @Query(value = "select * from rating where product_id = :productId" , nativeQuery = true)
  Page<RatingDto> getRatingsByProductId(@Param("productId") Long productId , Pageable pageable);

  

}
