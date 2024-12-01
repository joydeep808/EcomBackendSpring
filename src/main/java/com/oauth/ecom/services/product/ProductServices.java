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

  /**
   * This method creates a new product in the database.
   * 
   * @param productDto This is the object that contains the data that will be saved
   *                    in the database. It contains the following fields:
   *                    - categoryId: The id of the category that the product
   *                                    belongs to.
   *                    - name: The name of the product.
   *                    - images: A list of images that are associated with the
   *                              product.
   *                    - originalPrice: The original price of the product.
   *                    - price: The discounted price of the product.
   *                    - stock: The amount of stock that is available for the
   *                             product.
   *                    - description: A description of the product.
   *                    - returnPeriod: The time period during which the product
   *                                    can be returned.
   *                    - color: The color of the product.
   * @return ReqRes This is the response object that is returned by the method.
   *                It contains the following fields:
   *                - statusCode: The status code of the response. This can be
   *                              200, 400, 404, or 500.
   *                - message: A message that is associated with the response.
   *                - data: The data that is associated with the response. This
   *                       can be the product that was saved, or an error
   *                       message.
   */
  public ReqRes createProduct(CreateProductDto productDto) {
    ReqRes response = new ReqRes();
    try {
      // Check if the category id is null
      if (Optional.ofNullable(productDto.getCategoryId()).isEmpty()) {
        // If the category id is null, send a 400 error response
        response.sendErrorMessage(400, "Category id is required" ,"Category id not be null" );
        return response;
      }
      // Find the category by id
      Optional<Category> category = categoryRepo.findById(productDto.getCategoryId());
      // If the category is not found, send a 404 error response
      if (category == null || category.isEmpty()) {
        response.sendErrorMessage(404, "Category not found");
        return response;
      }
      // Check if a product with the same name already exists
      Products products = productRepo.findByName(productDto.getName());
      if (products != null) {
        // If a product with the same name already exists, send a 400 error response
        response.setStatusCode(400);
        response.setMessage("Product already saved with this name");
        return response;
      }
      // Create a new product
      Products product = Products.builder()
      .name(productDto.getName())
      .category(category.get())
      .images(productDto.getImages())
      .originalPrice(productDto.getOriginalPrice())
      .price(productDto.getPrice())
      .stock(productDto.getStock())
      .description(productDto.getDescription())
      .returnPeriod(productDto.getReturnPeriod())
      .color(productDto.getColor())
      .build();
      // Save the product
      Products savedProducts = productRepo.save(product);
      // Send a 200 success response with the saved product
      response.sendSuccessResponse(200, "Product saved successfully done!" , savedProducts);
      return response;
    } catch (Exception e) {
      // If an exception occurs, send a 500 error response
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
    // First check if the updateProductDto is null
    if (updateProductDto == null) {
      // If it is null, send a 400 error response
      response.sendErrorMessage(400, "Please provide atleast 1 value to update");
      return response;
    }
    try {
      // Find the product by id
      Products foundProduct = productRepo.findById(updateProductDto.getId()).get();
      // Check if the name is not null
      if (updateProductDto.getName() != null) {
        // Find the product by name
        Products product = productRepo.findByName(updateProductDto.getName());
        // If the product is not null, that means the name is already taken
        if (product != null) {
          // Send a 400 error response
          response.sendErrorMessage(400, "Product name is already store");
          return response;
        }
        else{
          // Set the name of the found product
          foundProduct.setName(updateProductDto.getName());
        }
      }
      // Check if the color is not null
      Optional.ofNullable(updateProductDto.getColor()).ifPresent(foundProduct::setColor);
      // Check if the stock is not null or greater than 0
      if (updateProductDto.getStock() != null || updateProductDto.getStock() >= 0) {
        // Set the stock of the found product
        foundProduct.setStock(updateProductDto.getStock());
      }
      // Check if the price is not null or greater than 0
      if (Optional.ofNullable(updateProductDto.getPrice()).isPresent() && updateProductDto.getPrice() > 0) {
        // Set the price of the found product
        foundProduct.setPrice(updateProductDto.getPrice());
      }
      // Check if the original price is not null or greater than 0
      if (Optional.ofNullable(updateProductDto.getOriginalPrice()).isPresent() && updateProductDto.getOriginalPrice() > 0) {
        // Set the original price of the found product
        foundProduct.setOriginalPrice(updateProductDto.getOriginalPrice());
      }
      // Check if the description is not null
      Optional.ofNullable(updateProductDto.getDescription()).ifPresent(foundProduct::setDescription);
      // Save the found product
      Products savedProduct = productRepo.save(foundProduct);
      // Send a 200 success response with the saved product
      response.sendSuccessResponse(200, "Update successfully done!" , savedProduct);
      return response;

    } catch (Exception e) {
      // If an exception occurs, send a 500 error response
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
  
  
  public ReqRes getAllProducts(int pageNumber, int size, String field) {
    ReqRes response = new ReqRes();
    
    // Ensure page number is not negative, adjust to zero-based index for pagination
    pageNumber = pageNumber < 0 ? 0 : pageNumber - 1;
    
    // Create a Sort object to sort the products in ascending order based on the specified field
    Sort sort = Sort.by(Sort.Direction.ASC, field);
    
    // Create a Pageable object with the given page number, size, and sort order
    Pageable pageable = PageRequest.of(pageNumber, size, sort);
    
    // Retrieve a page of products from the repository using the pageable object
    Page<Products> pagePost = this.productRepo.findAll(pageable);
    
    // Extract the list of products from the page
    List<Products> allProducts = pagePost.getContent();
    
    // Send a success response with the list of products
    response.sendSuccessResponse(200, "Products found successfully done!", allProducts);
    
    // Return the response object
    return response;
  }

}
