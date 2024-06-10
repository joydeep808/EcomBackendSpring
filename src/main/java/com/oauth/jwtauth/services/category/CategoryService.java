package com.oauth.jwtauth.services.category;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.entity.Category;
import com.oauth.jwtauth.entity.Products;
import com.oauth.jwtauth.repository.CategoryRepo;
import com.oauth.jwtauth.repository.ProductRepo;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepo categoryRepo;
  @Autowired 
  private ProductRepo productRepo;
  
  public ReqRes createCategory(Category category){
    ReqRes response = new ReqRes();
   try {
     Category FoundCategory = categoryRepo.findByName(category.getName());
     if ( FoundCategory != null) {
       response.setMessage("Category already found");
       response.setStatusCode(400);
     }
else{
  category.setName(category.getName());
  Category savedCategory = categoryRepo.save(category);
  response.setMessage("Category saved successfully done!");
  response.setStatusCode(200);
  response.setData(savedCategory);
}
     
   } catch (Exception e) {
      response.setMessage("Server is not reachable");
      response.setError(e.getMessage());
      response.setStatusCode(500);
   }
   return  response;
  }

public ReqRes updateCategoryName(Long id , String name){
  ReqRes response = new ReqRes();
  try {
    
    Category checkNameAlreadyExist = categoryRepo.findByName(name);
    if(checkNameAlreadyExist != null) {
      response.setMessage("Category name already exist ");
      response.setStatusCode(400);
      return response;
    }
    Optional<Category> foundCategory = categoryRepo.findById(name);
    if (foundCategory.isPresent()) {
        foundCategory.get().setName(name);
        categoryRepo.save(foundCategory.get());
        response.setMessage("Name updated succcessfully done!");
        response.setIsSuccess(true);
        response.setStatusCode(200);
        return response;
      }
      response.setMessage("Category not found");
      response.setStatusCode(404);
      return response;
  } catch (Exception e) {
    response.setStatusCode(500);
    response.setMessage("Server is not reachable");
    response.setError(e.getLocalizedMessage());
    return response;
  }
    
}

public ReqRes getCategoryProducts(String name){
  ReqRes response = new ReqRes();
 try {
   Category foundCategory = categoryRepo.findByName(name);
   if ( foundCategory == null ||  foundCategory.equals(null) ) {
     response.setStatusCode(404);
     response.setMessage("Category not found with this name");
   }
   else{
     List<Products> products = productRepo.findProductsByCategoryId(foundCategory.getId());
     if (products== null || products.isEmpty() || products.size() == 0) {
       response.setMessage("Products are not found with this category");
       response.setStatusCode(200);
     }
     else{
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
