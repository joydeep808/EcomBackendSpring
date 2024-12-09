package com.oauth.ecom.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "Rating")
@Data
@AllArgsConstructor
public class Rating {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotNull(message = "User id is required")
  private Long userId;
  @NotNull(message = "Rating is required")
  private Integer rating;
  @NotNull(message = "Product id is required")
  private Long productId;
  @NotNull(message = "Comment id is required")
  private String comment;
  private LocalDate createdAt;
  public Rating(){
    this.createdAt = LocalDate.now();
  }
  public Rating(Long userId, Long productId, Integer rating, String comment) {
    this.userId = userId;
    this.productId = productId;
    this.rating = rating;
    this.comment = comment;
  }
}
