package com.oauth.ecom.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  @Query(value = "SELECT *  FROM products  WHERE category = :id ", nativeQuery = true)
  Page<Products> findProductsByCategoryId(@Param("id")Long id , Pageable pageable);
  @Query(value = "SELECT * FROM products WHERE name ~ :name", nativeQuery = true)
  List<Products> findByNameRegex(@Param("name") String name);
  
  List<Products> findByCategory(Category category);
  
  @Query(value = "SELECT * from products WHERE id IN (:productsIds)" , nativeQuery = true)
  Optional<List<Products>> findByProductsIds(@Param("productsIds") List<Long> productsIds);

}