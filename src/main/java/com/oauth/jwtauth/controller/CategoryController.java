package com.oauth.jwtauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.entity.Category;
import com.oauth.jwtauth.services.category.CategoryService;
@Controller
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;
  @PostMapping("/create")
  
  public ResponseEntity<ReqRes> cCreate(@RequestBody Category category){
    ReqRes response = categoryService.createCategory(category);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes> updateCategory(@RequestBody Long id , String name){
    ReqRes response = categoryService.updateCategoryName(id, name);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/{name}")
  public ResponseEntity<ReqRes> getCategoryProducts(@PathVariable String name ){
    ReqRes response = categoryService.getCategoryProducts(name);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
}
