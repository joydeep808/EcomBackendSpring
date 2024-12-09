package com.oauth.ecom.services.ratings;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.rating.RatingDto;
import com.oauth.ecom.entity.Rating;
import com.oauth.ecom.entity.exceptions.thrown_exception.NotFoundException;
import com.oauth.ecom.repository.OrderRepo;
import com.oauth.ecom.repository.ProductRepo;
import com.oauth.ecom.repository.RatingRepo;
import com.oauth.ecom.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RatingService {

  private final ObjectMapper objectMapper;
  private final UserRepo userRepo;
  private final ProductRepo productRepo;
  private final RatingRepo ratingRepo;
  private final OrderRepo orderRepo;


  public boolean createRating(Rating rating){
    // I have to get the user and 
    // I have to check the orders for the specific user if found than only proceed further
    // Also that the user already rated or not if yes than dont have to save


    // String foundUser = userRepo.findByUserId(rating.getUser()).orElse(null);
    // if (foundUser != null) {
      
    // }
    ratingRepo.save(rating);
    return true;

  }
  public List<RatingDto> getRating(Long productId , int page ) throws NotFoundException, JsonProcessingException{
    Integer counter = 0;
    Pageable pageable  = PageRequest.of(page <= 0 ? 0 : page - 1, 10);
     Page<RatingDto> ratingsByProductId = ratingRepo.getRatingsByProductId(productId.longValue() , pageable);;
     
     if (ratingsByProductId.isEmpty() || ratingsByProductId.getTotalElements() == 0) {
       throw new NotFoundException("No ratings found", 404);
     }
     System.out.println(++counter);
     return ratingsByProductId.getContent();
  }
  
  
}
