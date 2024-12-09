package com.oauth.ecom.services.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.product.CreateProductDto;
import com.oauth.ecom.dto.product.ProductDto;
import com.oauth.ecom.dto.product.UpdateProductDto;
import com.oauth.ecom.entity.Category;
import com.oauth.ecom.entity.Products;
import com.oauth.ecom.mappers.ProductMapper;
import com.oauth.ecom.repository.CategoryRepo;
import com.oauth.ecom.repository.ProductRepo;
import com.oauth.ecom.services.redis.RedisService;
import com.oauth.ecom.util.ErrorException;
import com.oauth.ecom.util.ReqRes;

import lombok.RequiredArgsConstructor;




@Service
@RequiredArgsConstructor
public class ProductServices {
  
  
  private final ProductRepo productRepo;
  private final RedisService redisService;
  private final CategoryRepo categoryRepo;


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
  public ResponseEntity<ReqRes<ProductMapper>> createProduct(CreateProductDto productDto) {
    ReqRes<ProductMapper> response = new ReqRes<>();
    try {
     
      // Find the category by id
      Category category = categoryRepo.findByName(productDto.getCategoryName());
      // If the category is not found, send a 404 error response
      if (category == null) {
        return response.sendErrorMessage(404, "Category not found").sendResponseEntity();
        
      }
      // Check if a product with the same name already exists
      Products products = productRepo.findByName(productDto.getName());
      if (products != null) {
        // If a product with the same name already exists, send a 400 error response
       return  response.sendErrorMessage(400, "Product is already exist").sendResponseEntity();
      }
      // Create a new product
      Products product = Products.builder()
      .name(productDto.getName())
      .category(category)
      .images(productDto.getImages())
      .originalPrice(productDto.getOriginalPrice())
      .price(productDto.getPrice())
      .stock(productDto.getStock())
      .description(productDto.getDescription())
      .returnPeriod(productDto.getReturnPeriod())
      .color(productDto.getColor())
      .rating(productDto.getRating())
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

      // Save the product
     ProductMapper productMapper = (ProductMapper) productRepo.save(product);
      // Send a 200 success response with the saved product
      return response.sendSuccessResponse(200, "Product saved successfully done!"  , productMapper).sendResponseEntity();
      
    } catch (Exception e) {
      // If an exception occurs, send a 500 error response
      return response.sendErrorMessage(500, "Server not reachable" ).sendResponseEntity();
      
    }
  }
  public ResponseEntity<ReqRes<ProductDto>> getProductById(Long id){
    ReqRes<ProductDto> response = new ReqRes<>();
   Set<Object> value =  redisService.getMap(id.toString() ,ProductDto.class );
   if (value != null && value.size() > 0) {
    return response.sendSuccessResponse(200, "Product found successfully from redis" ,(ProductDto) value.stream().findFirst().get()).sendResponseEntity();
   }
      Products foundProduct =  productRepo.findById(id).orElse(null);
      if (foundProduct == null) {
        return response.sendErrorMessage(404, "Product not found with this id").sendResponseEntity();
        
      }
      ProductDto productDto = new ProductDto(foundProduct);
      redisService.saveInSingleQuery(id.toString(), productDto, 60);
      return response.sendSuccessResponse(200, "Product found successfully done!" , productDto).sendResponseEntity();
      
  }
  public ResponseEntity<ReqRes<ProductMapper>> updateProduct(UpdateProductDto updateProductDto){
    ReqRes<ProductMapper> response = new ReqRes<>();
    // First check if the updateProductDto is null
    if (updateProductDto == null) {
      // If it is null, send a 400 error response
      return response.sendErrorMessage(400, "Please provide atleast 1 value to update").sendResponseEntity();
      
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
          return response.sendErrorMessage(400, "Product name is already store").sendResponseEntity();
          
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
      ProductMapper savedProduct =(ProductMapper) productRepo.save(foundProduct);
      // Send a 200 success response with the saved product
      return response.sendSuccessResponse(200, "Update successfully done!" , savedProduct).sendResponseEntity();
      

    } catch (Exception e) {
      // If an exception occurs, send a 500 error response
      return response.sendErrorMessage(500, e.getMessage()).sendResponseEntity();
      
    }
  }
    public ResponseEntity<ReqRes<List<ProductMapper>>> getProductByName(String name){
    
    ReqRes<List<ProductMapper>> response =  new ReqRes<>();
       List<ProductMapper> foundProducts =  productRepo.findByNameRegex(name);

       if (foundProducts== null || foundProducts.isEmpty() || foundProducts.size()  == 0) {
        return response.sendErrorMessage(404, "No Product found").sendResponseEntity();
        
       }
       return response.sendSuccessResponse(200, "Products found successfully done" , foundProducts).sendResponseEntity();
       
  }
  public ResponseEntity<ReqRes<List<ProductMapper>>> getProductsByCategory(String categoryName) throws ErrorException{
    ReqRes<List<ProductMapper>> response = new ReqRes<>();
    Category category =   categoryRepo.findByName(categoryName);
    if (category == null) {
      throw new ErrorException("Category not found" ,404);
    }
   List<ProductMapper> products =  productRepo.findByCategory(category);
   if ( products == null || products.size() == 0) {
    throw new ErrorException("No Products found with this category" , 404);
   }
   return response.sendSuccessResponse(200, "Products found successfully done" , products).sendResponseEntity();
   
  }
  
  
  public ResponseEntity<ReqRes<List<ProductMapper>>> getAllProducts(int pageNumber, int size, String field) {
    ReqRes<List<ProductMapper>> response = new ReqRes<>();
    
    // Ensure page number is not negative, adjust to zero-based index for pagination
    pageNumber = pageNumber < 0 ? 0 : pageNumber - 1;
    
    // Create a Sort object to sort the products in ascending order based on the specified field
    Sort sort = Sort.by(Sort.Direction.ASC, field);
    
    // Create a Pageable object with the given page number, size, and sort order
    Pageable pageable = PageRequest.of(pageNumber, size, sort);
    
    // Retrieve a page of products from the repository using the pageable object
    Page<ProductMapper> pagePost = this.productRepo.findAllProductsInRandom(pageable);
    
    // Extract the list of products from the page
    List<ProductMapper> allProducts = pagePost.getContent();
    
    // Send a success response with the list of products
    return response.sendSuccessResponse(200, "Products found successfully done!", allProducts).sendResponseEntity();
    
    
  }

}
