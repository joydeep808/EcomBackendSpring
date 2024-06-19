package com.oauth.ecom.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "Category")
@Data
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotNull(message = "Name is required")
  private String name;
  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

 private LocalDateTime createdAt;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  @LastModifiedDate
  private LocalDateTime updatedAt;
  public Category(){
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
}
