package com.oauth.ecom.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oauth.ecom.entity.Category;
import com.oauth.ecom.entity.Products;
@Repository
public interface ProductRepo extends JpaRepository<Products , Object>{
  @Query(value = "select * from products where id =:id" , nativeQuery = true)
  Products findByProductId(@Param("id")Long id);
  Products findByName(String name);
  @Query(value = "SELECT *  FROM products  WHERE category = :id limit 1", nativeQuery = true)
  List<Products> findProductsByCategoryId(@Param("id")Long id);
  @Query(value = "SELECT * FROM products WHERE name ~ :name", nativeQuery = true)
  List<Products> findByNameRegex(@Param("name") String name);
  
  List<Products> findByCategory(Category category);
}