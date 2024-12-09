package com.oauth.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.entity.Category;
import com.oauth.ecom.entity.Products;
import com.oauth.ecom.services.category.CategoryService;
import com.oauth.ecom.util.ReqRes;
@Controller
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;
  @PostMapping("/create")
  public ResponseEntity<ReqRes<Category>> cCreate(@RequestBody Category category){
    return categoryService.createCategory(category);
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes<Object>> updateCategory(@RequestBody Long id , String name){
    return categoryService.updateCategoryName(id, name);
  }
  @GetMapping("/{name}")
  public ResponseEntity<ReqRes<Page<Products>>> getCategoryProducts(@PathVariable String name  , @RequestParam("page") Integer page){
    return categoryService.getCategoryProducts(name , page);
  }
}
