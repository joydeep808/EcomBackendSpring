package com.oauth.ecom.dto.rating;

import java.time.LocalDate;

public interface RatingDto {
   Long getId();
   Long getUserId();
   Integer getRating();
   Long getProductId();
   String getComment();
   String getCreatedAt();
}
