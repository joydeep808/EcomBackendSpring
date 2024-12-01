package com.oauth.ecom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oauth.ecom.entity.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category , Object> {
  @Query(value = "SELECT * from category where name ~:name" , nativeQuery=true)
  Category findByName(@Param("name") String name );
}
