package com.oauth.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.product.*;
import com.oauth.ecom.entity.Products;
import com.oauth.ecom.mappers.ProductMapper;
import com.oauth.ecom.services.product.ProductServices;
import com.oauth.ecom.util.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
  @Autowired
  ProductServices productServices;

  @PostMapping("/create")
  public ResponseEntity<ReqRes<ProductMapper>> createProduct(@RequestBody @Valid CreateProductDto products) {

    return productServices.createProduct(products);
  }


  @PutMapping("/update")
  public ResponseEntity<ReqRes<ProductMapper>> updateProduct(@RequestBody UpdateProductDto updateProductDto) {
    return productServices.updateProduct(updateProductDto);
  }


  @GetMapping("/id")
  public ResponseEntity<ReqRes<ProductDto>> getProductById(@RequestParam("productId") Long id) {
    return productServices.getProductById(id);
  }

  @GetMapping("/name")
  public ResponseEntity<ReqRes<List<ProductMapper>>> getProductByName(@RequestParam("name") String name) {
    return productServices.getProductByName(name);
  }

  @GetMapping("/p/category")
  public ResponseEntity<ReqRes<List<ProductMapper>>> getProductsByCategory(
      @RequestParam(value = "name", required = true, defaultValue = "") String name) throws ErrorException {
    return productServices.getProductsByCategory(name);
  }

  @GetMapping("/p/all")
  public ResponseEntity<ReqRes<List<ProductMapper>>> getAllProducts(
      @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
      @RequestParam(value = "size", defaultValue = "30", required = false) Integer size,
      @RequestParam(value = "field", defaultValue = "", required = false) String field) throws ErrorException {
    return productServices.getAllProducts(page, size, field);
  }

}
