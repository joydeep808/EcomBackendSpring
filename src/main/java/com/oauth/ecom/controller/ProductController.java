package com.oauth.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.ReqRes;
import com.oauth.ecom.dto.product.*;
import com.oauth.ecom.services.product.ProductServices;
import com.oauth.ecom.util.ErrorException;

import jakarta.validation.Valid;






@Controller
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
  @Autowired
  private ProductServices productServices;

  @PostMapping("/create")
  public ResponseEntity<ReqRes> createProduct(@RequestBody @Valid CreateProductDto products){
    ReqRes response = productServices.createProduct(products);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes> updateProduct(@RequestBody UpdateProductDto updateProductDto){
    ReqRes response = productServices.updateProduct(updateProductDto);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/{id}")
  public ResponseEntity<ReqRes> getProductById(@PathVariable Long id){
    ReqRes response = productServices.getProductById(id);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/p/{name}")
  public ResponseEntity<ReqRes> getProductByName(@PathVariable String name) {
    ReqRes response = productServices.getProductByName(name);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/p/category")
  public ResponseEntity<ReqRes> getProductsByCategory(@RequestParam(value = "name" ,required = true, defaultValue = "") String name )throws ErrorException {
    ReqRes response = productServices.getProductsByCategory(name);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  
  @GetMapping("/p/all")
  public ResponseEntity<ReqRes> getAllProducts(
    @RequestParam(value = "page" ,defaultValue = "1",required = false ) Integer page,
    @RequestParam(value = "size" ,defaultValue = "10",required = false ) Integer size,
    @RequestParam(value = "field" ,defaultValue = "",required = false ) String field
  )throws ErrorException{
    ReqRes response = productServices.getAllProducts(page,size,field);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }
}
