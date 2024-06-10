package com.oauth.jwtauth.services.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.product.CreateProductDto;
import com.oauth.jwtauth.dto.product.UpdateProductDto;
import com.oauth.jwtauth.entity.Category;
import com.oauth.jwtauth.entity.Products;
import com.oauth.jwtauth.repository.CategoryRepo;
import com.oauth.jwtauth.repository.ProductRepo;

@Service
public class ProductServices {
  
  @Autowired 
  private ProductRepo productRepo;
  @Autowired
  private CategoryRepo categoryRepo;

  public ReqRes createProduct(CreateProductDto productDto) {
    ReqRes res = new ReqRes();
    try {
      if (Optional.ofNullable(productDto.getCategoryId()).isEmpty()) {
        res.setMessage("Category id is required");
        res.setStatusCode(400);
        res.setError("Category id not be null");
        return res;
      }
     Optional<Category> category = categoryRepo.findById(productDto.getCategoryId());
     if (category == null || category.isEmpty()) {
      res.setMessage("Category not found");
      res.setStatusCode(404 );
      return res;
     }
     Products products = productRepo.findByName(productDto.getName());
     if (products != null) {
        res.setStatusCode(400);
        res.setMessage("Product already saved with this name");
        return res;
     }
     Products product = new Products();
     product.setName(productDto.getName());
     product.setCategory(category.get());
     product.setImages(productDto.getImages());
     product.setOriginalPrice(productDto.getOriginalPrice());
     product.setPrice(productDto.getPrice());
     product.setStock(productDto.getStock());
     product.setDescription(productDto.getDescription());
     product.setReturnPeriod(productDto.getReturnPeriod());
     product.setColor(productDto.getColor());
     Products savedProducts = productRepo.save(product);
     res.setStatusCode(200);
     res.setMessage("Product saved successfully done ");
     res.setIsSuccess(true);
     res.setData(savedProducts);
     return res;
    } catch (Exception e) {
      res.setMessage("Server not reachable");
      res.setStatusCode(500);
      res.setError(e.getLocalizedMessage());
      return res;
    }
  }
  public ReqRes getProductById(Long id){

    ReqRes response = new ReqRes();
    try {
      Products foundProduct = productRepo.findById(id).get();
      response.setMessage("Product found successfully done!");
      response.setStatusCode(200);
      response.setIsSuccess(true);
      response.setData(foundProduct);
      return response;
    } catch (Exception e) {
      response.setMessage("Server not reachable");
      response.setError(e.getLocalizedMessage());
      response.setStatusCode(500);
      return response;
    }
  }
  public ReqRes updateProduct(UpdateProductDto updateProductDto){
  ReqRes response = new ReqRes();
  if (updateProductDto == null) {
    response.setMessage("Please provide atleast 1 value to update");
    response.setStatusCode(400);
    return response;
  }
  try {
    Products foundProduct = productRepo.findById(updateProductDto.getId()).get();
    if (updateProductDto.getName() != null) {
    Products product = productRepo.findByName(updateProductDto.getName());
    if (product != null) {
      response.setError("Product name is already store");
    }
    else{
      foundProduct.setName(updateProductDto.getName());
    }
    }
    Optional.ofNullable(updateProductDto.getColor()).ifPresent(foundProduct::setColor);
    if (updateProductDto.getStock() != null || updateProductDto.getStock() >= 0) {
      foundProduct.setStock(updateProductDto.getStock());
    }
    if ( Optional.ofNullable(updateProductDto.getPrice() > 0).isPresent()) {
      foundProduct.setPrice(updateProductDto.getPrice());
    }  
     if ( Optional.ofNullable(updateProductDto.getOriginalPrice() > 0).isPresent()) {
      foundProduct.setOriginalPrice(updateProductDto.getOriginalPrice());
    }
    Optional.ofNullable(updateProductDto.getDescription()).ifPresent(foundProduct::setDescription);
   Products savedProduct =  productRepo.save(foundProduct);
    response.setMessage("Updated successfully done!");
    response.setStatusCode(200);
    response.setIsSuccess(true);
    response.setData(savedProduct);
    return response;

  } catch (Exception e) {
    response.setError(e.getLocalizedMessage());
    response.setMessage(e.getMessage());
    response.setStatusCode(500);
    return response;
  }
}
  public ReqRes getProductByName(String name){
    ReqRes response =  new ReqRes();
    try {
       List<Products> foundProducts =  productRepo.findByNameRegex(name);
       if (foundProducts== null || foundProducts.isEmpty() || foundProducts.size()  == 0) {
        response.setMessage("No Product found ");        
        response.setStatusCode(404);
        return response;
       }
       response.setMessage("Products found successfully done");
       response.setData(foundProducts);
       response.setStatusCode(200);
       response.setIsSuccess(true);
       return response;
    } catch (Exception e) {
    response.setMessage(e.getMessage());
    response.setError(e.getLocalizedMessage());
    response.setStatusCode(500);
    return response;
    }
  }

}
