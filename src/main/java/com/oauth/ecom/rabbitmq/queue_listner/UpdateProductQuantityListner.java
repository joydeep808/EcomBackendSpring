package com.oauth.ecom.rabbitmq.queue_listner;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.entity.Products;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.repository.ProductRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpdateProductQuantityListner {
  private final ProductRepo productRepo;
  private final ObjectMapper objectMapper;


  @RabbitListener(queues = RabbitMqConfig.UPDATE_PRODUCT_QUEUE)
  public void updateProductQueueListner(String message) throws JsonMappingException, JsonProcessingException{
   Map<Long , Long> products =  objectMapper.readValue(message, new TypeReference<Map<Long , Long>>() {});
    this.UpdateProductQuantity(products);
  }

  public void UpdateProductQuantity(Map<Long , Long> products) {
    // The products map passed to this method has the product id as the key and the quantity to be decremented as the value.
    // First, we need to get the list of ids from the keyset of the map.
    List<Long> productIds= products.keySet().stream().toList();
    
    // Then we need to use the product repo to find all products with the ids in the productIds list.
    List<Products> foundProducts = productRepo.findByProductsIds(productIds ).orElse(null);

    // If the list of found products is null or empty, then we should return immediately since we have nothing to update.
    if (foundProducts == null || foundProducts.isEmpty()) {
      return;
    }

    // Otherwise, we need to iterate over the list of found products and decrement the stock of each product by the quantity
    // in the products map associated with the product id.
    for (Products currentProduct : foundProducts) {
      currentProduct.setStock(currentProduct.getStock() -  products.get(currentProduct.getId()));
    }

    // Finally, we need to save all of the updated products to the database.
    productRepo.saveAll(foundProducts);
    return;
  }
}
