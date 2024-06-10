package com.oauth.jwtauth.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oauth.jwtauth.entity.Products;
@Repository
public interface ProductRepo extends JpaRepository<Products , Object>{
  @Query(value = "select * from products where id =:id" , nativeQuery = true)
  Products findByProductId(@Param("id")Long id);
  // Products findByName(String name);
  // List<Products> findSellerProducts(Long seller);
  // @Query("SELECT p from Products p where p.id =:id and p.name =:name")
  // Products findByIdAndName(@Param("id")String userId , @Param("name") String name);
  // Products findByNameAndSeller(String name , String seller);
  Products findByName(String name);
  @Query(value = "SELECT *  FROM products  WHERE category = :id limit 1", nativeQuery = true)
  // @Query(value = "SELECT * FROM products  WHERE category = :id limit 1", nativeQuery = true) limt use in this case
  List<Products> findProductsByCategoryId(@Param("id")Long id);
  @Query(value = "SELECT * FROM products WHERE name ~ :name", nativeQuery = true)
  List<Products> findByNameRegex(@Param("name") String name);
  // @Query(value = "select p.id , p.name , p.stock ,p.price from products where id =:ids")m
}