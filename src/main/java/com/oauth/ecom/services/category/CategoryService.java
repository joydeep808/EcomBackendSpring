package com.oauth.ecom.services.category;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.oauth.ecom.entity.Category;
import com.oauth.ecom.entity.Products;
import com.oauth.ecom.repository.CategoryRepo;
import com.oauth.ecom.repository.ProductRepo;
import com.oauth.ecom.util.ReqRes;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepo categoryRepo;
  @Autowired
  private ProductRepo productRepo;

  public ReqRes createCategory(Category category) {
    ReqRes response = new ReqRes();
      Category FoundCategory = categoryRepo.findByName(category.getName());
      if (FoundCategory != null) {
        response.sendErrorMessage(400, "Category already found");
      } else {
        category.setName(category.getName());
        Category savedCategory = categoryRepo.save(category);
        response.sendSuccessResponse(201, "Category saved successfully done", savedCategory);
        response.setData(savedCategory);
      }
    return response;
  }

  public ReqRes updateCategoryName(Long id, String name) {
    ReqRes response = new ReqRes();

    try {

      Category checkNameAlreadyExist = categoryRepo.findByName(name);
      if (checkNameAlreadyExist != null) {
        response.sendErrorMessage(400, "Category name already exist");
        return response;
      }
      Optional<Category> foundCategory = categoryRepo.findById(name);
      if (foundCategory.isPresent()) {
        foundCategory.get().setName(name);
        categoryRepo.save(foundCategory.get());
        response.sendSuccessResponse(201, "Category name updated successfully done");
        return response;
      }
      response.sendErrorMessage(404, "Category not found");
      return response;
    } catch (Exception e) {
      response.setStatusCode(500);
      response.setMessage("Server is not reachable");
      response.setError(e.getLocalizedMessage());
      return response;
    }

  }

  public ReqRes getCategoryProducts(String name, Integer pageNumber) {

    ReqRes response = new ReqRes();

    try {
      Pageable pageable = PageRequest.of(pageNumber < 0 ? 0 : pageNumber - 1, 10);
      Category foundCategory = categoryRepo.findByName(name);
      if (foundCategory == null || foundCategory.equals(null)) {
        response.setStatusCode(404);
        response.setMessage("Category not found with this name");
      } else {
        Page<Products> products = productRepo.findProductsByCategoryId(foundCategory.getId(), pageable);
        if (products.isEmpty() || products.getTotalElements() == 0) {
          response.setMessage("Products are not found with this category");
          response.setStatusCode(200);
        } else {
          response.setMessage("Products found");
          response.setStatusCode(200);
          response.setIsSuccess(true);
          response.setData(products);
        }
      }
    } catch (Exception e) {
      response.setMessage("Server not reachable ");
      response.setStatusCode(500);
      response.setError(e.getLocalizedMessage());

    }
    return response;
  }

}
