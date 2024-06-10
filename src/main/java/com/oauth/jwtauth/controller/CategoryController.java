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
    return ResponseEntity.ok(categoryService.createCategory(category));
    // return ResponseEntity.ok("hisf");
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes> updateCategory(@RequestBody Long id , String name){
    return  ResponseEntity.ok(categoryService.updateCategoryName(id, name));
  }
  @GetMapping("/{name}")
  public ResponseEntity<ReqRes> getCategoryProducts(@PathVariable String name ){
    return ResponseEntity.ok(categoryService.getCategoryProducts(name));
  }
}
