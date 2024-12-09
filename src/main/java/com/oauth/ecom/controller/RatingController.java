package com.oauth.ecom.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oauth.ecom.dto.rating.RatingDto;
import com.oauth.ecom.entity.Rating;
import com.oauth.ecom.entity.exceptions.thrown_exception.NotFoundException;
import com.oauth.ecom.services.ratings.RatingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/rating")
public class RatingController {

  private final RatingService ratingService;

  @PostMapping("/create")
  public boolean createRating(@RequestBody @Valid Rating rating) {
    return ratingService.createRating(rating);
  }
  @GetMapping("/get")
  public List<RatingDto> getRatingsByProductId(@RequestParam("id") Long id,
      @RequestParam(value = "page", defaultValue = "0") int page) throws NotFoundException, JsonProcessingException {
    return ratingService.getRating(id, page);
  }
}
