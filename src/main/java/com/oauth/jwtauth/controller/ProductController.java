package com.oauth.jwtauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.product.CreateProductDto;
import com.oauth.jwtauth.dto.product.UpdateProductDto;
import com.oauth.jwtauth.services.product.ProductServices;

import jakarta.validation.Valid;
@Controller
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
  @Autowired
  private ProductServices productServices;

  @PostMapping("/create")
  public ResponseEntity<ReqRes> createProduct(@RequestBody @Valid CreateProductDto products){
    return ResponseEntity.ok(productServices.createProduct(products));
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes> updateProduct(@RequestBody UpdateProductDto updateProductDto){
    return ResponseEntity.ok(productServices.updateProduct(updateProductDto));
  }
  @GetMapping("/{id}")
  public ResponseEntity<ReqRes> getProductById(@PathVariable Long id){
    return ResponseEntity.ok(productServices.getProductById(id));
  }
  @GetMapping("/p/{name}")
  public ResponseEntity<ReqRes> getProductByName(@PathVariable String name){
    ReqRes response= productServices.getProductByName(name);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }
}
