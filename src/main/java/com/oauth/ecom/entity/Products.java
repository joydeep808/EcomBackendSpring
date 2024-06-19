package com.oauth.ecom.entity;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.oauth.ecom.util.LocalDateTimeDeserializer;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "Products" , indexes = { @Index(name="idx_name" , columnList = "name")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class Products {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotNull(message = "Name is required ")
  private String name;
  @NotNull( message= "Description is required")
  private String description;
  @NotNull(message = "Please provide the return period")
  private int returnPeriod;
  private List<String> images;
  @ManyToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER) @JoinColumn(name = "category")
  @JsonIgnore
  private Category category;
  @NotNull(message = "Original price is required")
  private float originalPrice;
  @NotNull(message = "Price is required")
  private float price;
  @NotNull(message = "Color is required")
  private String color;
  @NotNull(message = "Stock should not be null")
  private Long stock;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)

@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @CreatedDate
  
  private LocalDateTime createdAt;
@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
@JsonDeserialize(using = LocalDateTimeDeserializer.class)

  @LastModifiedDate
  private LocalDateTime updatedAt;
  public Products(){
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

}
