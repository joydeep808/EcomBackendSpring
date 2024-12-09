package com.oauth.ecom.services.category;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oauth.ecom.entity.*;
import com.oauth.ecom.repository.*;
import com.oauth.ecom.util.ReqRes;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepo categoryRepo;
  @Autowired
  private ProductRepo productRepo;

  public ResponseEntity<ReqRes<Category>> createCategory(Category category) {
    ReqRes<Category> response = new ReqRes<>();
      Category FoundCategory = categoryRepo.findByName(category.getName());
      if (FoundCategory != null) {
        return response.sendErrorMessage(400, "Category already found").sendResponseEntity();
      } else {
        category.setName(category.getName());
        categoryRepo.save(category);
        return response.sendSuccessResponse(201, "Category saved successfully done").sendResponseEntity();
      }
  }

  public ResponseEntity<ReqRes<Object>> updateCategoryName(Long id, String name) {
    ReqRes<Object> response = new ReqRes<>();


      Category checkNameAlreadyExist = categoryRepo.findByName(name);
      if (checkNameAlreadyExist != null) {
       return  response.sendErrorMessage(400, "Category name already exist").sendResponseEntity();
      }
      Optional<Category> foundCategory = categoryRepo.findById(name);
      if (foundCategory.isPresent()) {
        foundCategory.get().setName(name);
        categoryRepo.save(foundCategory.get());
       return  response.sendSuccessResponse(201, "Category name updated successfully done").sendResponseEntity();
      }
      return response.sendErrorMessage(404, "Category not found").sendResponseEntity();

  }

  public ResponseEntity<ReqRes<Page<Products>>> getCategoryProducts(String name, Integer pageNumber) {

    ReqRes<Page<Products>> response = new ReqRes<>();

      Pageable pageable = PageRequest.of(pageNumber < 0 ? 0 : pageNumber - 1, 10);
      Category foundCategory = categoryRepo.findByName(name);
      if (foundCategory == null || foundCategory.equals(null)) {
        return response.sendErrorMessage(404 , "Category not found with this name").sendResponseEntity();
      } else {
        Page<Products> products = productRepo.findProductsByCategoryId(foundCategory.getId(), pageable);
        if (products.isEmpty() || products.getTotalElements() == 0) {
         return  response.sendErrorMessage(404, "Products are not found with this category").sendResponseEntity();
        } else {
          return response.sendSuccessResponse(200 , "Products found"  , products).sendResponseEntity();
        }
      }

}

}