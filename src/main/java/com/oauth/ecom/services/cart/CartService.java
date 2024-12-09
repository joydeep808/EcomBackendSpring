package com.oauth.ecom.services.cart;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.cart.*;
import com.oauth.ecom.dto.product.ProductParseDto;
import com.oauth.ecom.entity.*;
import com.oauth.ecom.entity.exceptions.thrown_exception.NotFoundException;
import com.oauth.ecom.mappers.cart.CartItemsMapper;
import com.oauth.ecom.mappers.cart.CartMapper;
import com.oauth.ecom.rabbitmq.MessageSender;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.repository.*;
// import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.services.redis.RedisService;
import com.oauth.ecom.util.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
  private final JwtInterceptor jwtInterceptor;
  private final CartRepo cartRepo;
  private final ProductRepo productRepo;
  private final CartItemsRepo cartItemsRepo;
  private final RedisService redisService;
  private final MessageSender messageSender;
  private final ObjectMapper objectMapper;

  // private KafkaService kafkaService;

  public ResponseEntity<ReqRes<Object>> addProductInCart(HttpServletRequest httpServletRequest, AddCartQuantityDto cartItemsDto) throws Exception{
    ReqRes<Object> response = new ReqRes<>();
      // First, we need to find the product that the user wants to add to their cart.
      Products product = productRepo.findById(cartItemsDto.getProductId()).orElse(null);
      if (product == null) {
        // If the product doesn't exist, we return a 404 error.
        return response.sendErrorMessage(404, "Product not found").sendResponseEntity();
      }
      // Check if the product is out of stock.
      if (product.getStock() <= 0) {
        // If the product is out of stock, we return a 400 error.
        return response.sendErrorMessage(400, "Product is out of stock").sendResponseEntity();
      }
      // Check if the user is trying to add more than the product has in stock.
      if (product.getStock() - cartItemsDto.getQuantity() < 0) {
        // If the user is trying to add more than the product has in stock,
        // we return a 400 error.
        return response.sendErrorMessage(400, "Please try again with an lower stock").sendResponseEntity();
      }
      // Extract the user's ID from the request.
      Long user = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
      // Create an object that contains the product ID, user ID and quantity.
      ProductParseDto productDetails = new ProductParseDto(product.getId(), user, cartItemsDto.getQuantity());
      // Send the object to the RabbitMQ queue.
      // This will trigger a message to be sent to the consumer that will update the
      // user's cart.
      messageSender.sendMessageToQueue(RabbitMqConfig.CART_QUEUE, objectMapper.writeValueAsString(productDetails));
      // kafkaService.sendMessage("cart_topic" , productDetails );
      // Return a success response with a message.
      return response.sendSuccessResponse(200, "Cart will be updated shortly").sendResponseEntity();
    }

  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<ReqRes<Object>> removeProductFromCart(HttpServletRequest httpServletRequest) throws Exception {
    ReqRes<Object> response = new ReqRes<>();

    // Extract the user's ID from the request.
    Long id = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
    // Find the user's cart.
    Cart cart = cartRepo.findByUser(id);
    // If the cart is empty, return a 400 error.
    if (cart.getCartItems().size() == 0) {
      throw new NotFoundException("No Products found to remove" , 404);
    }
    // Get all the cart items from the cart.
    List<CartItems> cartItems = cart.getCartItems();
    // Delete all the cart items from the database.
    cartItemsRepo.deleteAll(cartItems);
    // Set the cart total to 0, remove any coupon code, and set the discounted total
    // to 0.
    cart.setCartTotal(0);
    cart.setCouponCode(null);
    cart.setDiscountCartTotal(0);
    // Save the cart.
    cartRepo.save(cart);
    // Return a success response with a message.
    return response.sendSuccessResponse(200, "Successfully removed").sendResponseEntity();

  }

  @Transactional
  public ResponseEntity<ReqRes<Cart>> decrementCartQuantity(HttpServletRequest httpServletRequest, DecrementCartQuantityDto cartQuantityDto)
      throws Exception {
    ReqRes<Cart> response = new ReqRes<>();
      // Get the user ID from the request.
      long userId = jwtInterceptor.getIdFromJwt(httpServletRequest);

      // Find the user's cart.
      Cart cart = cartRepo.findByUser(userId);

      // If the cart does not exist, return a 404 error.
      if (cart == null || cart.getCartItems().isEmpty()) {
        throw new NotFoundException("Cart not found", 404);
      }

      // Stream the cart items and check if a product matches the ID we are looking
      // for.
      // If a match is found, decrement the quantity and update the cart total.
      // Save the updated cart.
      // boolean isCartUpdated = cart.getCartItems().stream()
      //     .filter(pro -> pro.getProduct().getId() == cartQuantityDto.getProductId()).findFirst().map(p -> {
      //       p.setQuantity(p.getQuantity() - 1);
      //       cart.setCartTotal(cart.getCartTotal() - p.getProduct().getPrice());
      //       cartRepo.save(cart);
      //       return true;
      //     }).orElse(false);

      // If the cart was updated, return a 200 success response with the updated cart.
      // Otherwise, return a 404 error.
      // if (isCartUpdated) {
      //   return response.sendSuccessResponse(200, "Quantity decrement successfully done!", cart).sendResponseEntity();
      // }
      return response.sendErrorMessage(404, "Product not found to decrement the quantity").sendResponseEntity();

  }

  public ResponseEntity<ReqRes<List<CartItems>>> getCartInfo(HttpServletRequest httpServletRequest) throws Exception{
    ReqRes<List<CartItems>> response = new ReqRes<>();
      // Get the user's ID from the JWT token in the HTTP request.
      Long id = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);

      // Check if the user's cart is in the redis cache.
      // If the cart is in the cache, return it to the user.
      @SuppressWarnings("unchecked")
      List<CartItems> CacheCartInfo =(List<CartItems>) redisService.getData("CART" + id,Object.class);
      if (CacheCartInfo != null) {
       return  response.sendSuccessResponse(200, "Cart Products found from redis", CacheCartInfo).sendResponseEntity();
      }

      // If the cart is not in the cache, find the user's cart in the database.
      Cart cartinfo = cartRepo.findByUser(id);

      // If the cart does not exist in the database, or if it is empty, return a 400
      // error.
      if (cartinfo == null || cartinfo.getCartItems().isEmpty() || cartinfo.getCartItems().size() == 0) {
        return response.sendErrorMessage(404, "Empty Cart").sendResponseEntity();
      }

      // Save the user's cart in the redis cache.
      redisService.saveInSingleQuery("CART" + id, cartinfo.getCartItems(), 20);

      // Return the user's cart.
      return response.sendSuccessResponse(200, "Cart Products found from redis", cartinfo.getCartItems()).sendResponseEntity();

  }

  public Boolean updateCart(ProductParseDto valueObject) {
    if (valueObject != null) {
      // Get the user ID from the object.
      long user = (long) valueObject.getUser();

      // Find the user's cart.
      Cart cart = cartRepo.findByUser(user);
      // If the cart doesn't exist, or if it is empty, return.
      if (cart == null) {
        return false;
      } 
      List<CartItemsMapper> foundCartItems = cartItemsRepo.findCartItemsProducts(cart.getId());;
      // Check if the product is already in the user's cart.
     if (foundCartItems.size() > 0) {
        for (CartItemsMapper cartItems : foundCartItems) {
          // If the product is already in the cart, update the quantity.
          if (cartItems.getProductId().equals(valueObject.getProductId())) {
            CartItems existingCartItems = cartItemsRepo.findByCartIdAndProduct(cart.getId(), valueObject.getProductId().longValue());
            existingCartItems.setQuantity(existingCartItems.getQuantity() + valueObject.getQuantity());
           float total = cartItems.getProductPrice() * valueObject.getQuantity();
            cart.setCartTotal(cart.getCartTotal() + total);
            cart.setDiscountCartTotal(cart.getDiscountCartTotal() +total);
            cartRepo.save(cart);
            return true; 
          }
        }
     }

      // If the product was not found, find the product in the database.
      Products product = productRepo.findById(valueObject.getProductId()).orElse(null);

      // If the product doesn't exist, or if its stock is 0, return.
      if (product == null || product.getStock() <= 0) {
        return false;
      }

      // Create a new cart item and set its product and cart.
      CartItems cartItems = new CartItems();
      cartItems.setProductId(valueObject.getProductId());
      cartItems.setCart(cart.getId());
      
      // If the stock of the product is less than the quantity in the value object,
      // set the quantity of the cart item to 1.
      // Otherwise, set the quantity of the cart item to the quantity in the value
      // object.
      if (product.getStock() - valueObject.getQuantity() < 0) {
        cartItems.setQuantity(1);
      } else {
        cartItems.setQuantity(valueObject.getQuantity());
      }

      // Save the cart item.
      cartItemsRepo.saveAndFlush(cartItems);

      // Update the cart total.
      float total = cartItems.getQuantity() * product.getPrice();
      cart.setCartTotal(cart.getCartTotal() + total);
      cart.setDiscountCartTotal(cart.getDiscountCartTotal()+total);

      // Save the cart.
      cartRepo.save(cart);
      return true;
    }
    return false;
  }

}