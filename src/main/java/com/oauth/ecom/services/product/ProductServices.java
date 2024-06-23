package com.oauth.ecom.services.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.product.CreateProductDto;
import com.oauth.ecom.dto.product.UpdateProductDto;
import com.oauth.ecom.entity.Category;
import com.oauth.ecom.entity.Products;
import com.oauth.ecom.repository.CategoryRepo;
import com.oauth.ecom.repository.ProductRepo;
import com.oauth.ecom.util.ErrorException;
import com.oauth.ecom.util.ReqRes;




@Service
public class ProductServices {
  
  @Autowired 
  private ProductRepo productRepo;
  @Autowired
  private CategoryRepo categoryRepo;

  public ReqRes createProduct(CreateProductDto productDto) {
    ReqRes response = new ReqRes();
    try {
      if (Optional.ofNullable(productDto.getCategoryId()).isEmpty()) {
        response.sendErrorMessage(400, "Category id is required" ,"Category id not be null" );
        return response;
      }
     Optional<Category> category = categoryRepo.findById(productDto.getCategoryId());
     if (category == null || category.isEmpty()) {
      response.sendErrorMessage(404, "Category not found");
      return response;
     }
     Products products = productRepo.findByName(productDto.getName());
     if (products != null) {
        response.setStatusCode(400);
        response.setMessage("Product already saved with this name");
        return response;
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
     response.sendSuccessResponse(200, "Product saved successfully done!" , savedProducts);
     return response;
    } catch (Exception e) {
      response.sendErrorMessage(500, "Server not reachable" , e.getLocalizedMessage());
      return response;
    }
  }
  public ReqRes getProductById(Long id){
    ReqRes response = new ReqRes();

    try {
      Products foundProduct = productRepo.findById(id).orElse(null);
      if (foundProduct == null) {
        response.sendErrorMessage(404, "Product not found with this id");
        return response;
      }
      response.sendSuccessResponse(200, "Product found successfully done!" , foundProduct);
      return response;
    } catch (Exception e) {
      response.sendErrorMessage(500, "Server not reachable" , e.getLocalizedMessage());
      return response;
    }
  }
  public ReqRes updateProduct(UpdateProductDto updateProductDto){
    ReqRes response = new ReqRes();
  if (updateProductDto == null) {
    response.sendErrorMessage(400, "Please provide atleast 1 value to update");
    return response;
  }
  try {
    Products foundProduct = productRepo.findById(updateProductDto.getId()).get();
    if (updateProductDto.getName() != null) {
    Products product = productRepo.findByName(updateProductDto.getName());
    if (product != null) {
      response.sendErrorMessage(400, "Product name is already store");
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
   response.sendSuccessResponse(200, "Update successfully done!" , savedProduct);
    return response;

  } catch (Exception e) {
    response.sendErrorMessage(500, e.getMessage());
    return response;
  }
}
  public ReqRes getProductByName(String name){
    ReqRes response =  new ReqRes();
    try {
       List<Products> foundProducts =  productRepo.findByNameRegex(name);

       if (foundProducts== null || foundProducts.isEmpty() || foundProducts.size()  == 0) {
        response.sendErrorMessage(404, "No Product found");
        return response;
       }
       response.sendSuccessResponse(200, "Products found successfully done" , foundProducts);
       return response;
    } catch (Exception e) {
      response.sendErrorMessage(500, e.getMessage());
    return response;
    }
  }
  public ReqRes getProductsByCategory(String categoryName) throws ErrorException{
    ReqRes response = new ReqRes();
    try {
    Category category =   categoryRepo.findByName(categoryName);
    if (category == null) {
      throw new ErrorException("Category not found" ,404);
    }
   List<Products> products =  productRepo.findByCategory(category);
   if ( products == null || products.size() == 0) {
    throw new ErrorException("No Products found with this category" , 404);
   }
   response.sendSuccessResponse(200, "Products found successfully done" , products);;
   return response;
    } catch (ErrorException e ) {
      throw new ErrorException(e.getMessage() , e.getStatusCode());
    }
  }
  public ReqRes getAllProducts(int pageNumber , int size  ,String field){
    ReqRes response = new ReqRes();
    pageNumber = pageNumber > 0 ? pageNumber - 1 : 0;
    Sort sort = Sort.by(Sort.Direction.ASC , field);
    Pageable pageable = PageRequest.of(pageNumber , size ,sort);
    Page<Products> pagePost = this.productRepo.findAll(pageable);
    System.out.println();
    List<Products> allProducts = pagePost.getContent();
    // List<Products> prods = allProducts.stream().map((p)->p).collect(Collectors.toList());
    response.sendSuccessResponse(200 ,"Products found successfully done!" , allProducts );
    return response;
  }

}
