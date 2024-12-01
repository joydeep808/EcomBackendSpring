package com.oauth.ecom.services.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.cart.AddCartQuantityDto;
import com.oauth.ecom.dto.cart.DecrementCartQuantityDto;
import com.oauth.ecom.dto.product.ProductParseDto;
import com.oauth.ecom.entity.*;
import com.oauth.ecom.rabbitmq.MessageSender;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.repository.*;
// import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.services.redis.RedisService;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;

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

  /**
   * This function is responsible for adding a product to a user's cart.
   * 
   * @param httpServletRequest The HTTP request from which we can extract the
   *                           user's ID.
   * @param cartItemsDto       The product ID and quantity that the user wants to
   *                           add to their cart.
   * 
   * @return A response object that contains the status code and a message.
   * 
   * @throws Exception If there is an error while processing the request.
   */
  public ReqRes addProductInCart(HttpServletRequest httpServletRequest, AddCartQuantityDto cartItemsDto) {
    ReqRes response = new ReqRes();
    try {
      // First, we need to find the product that the user wants to add to their cart.
      Products product = productRepo.findById(cartItemsDto.getProductId()).orElse(null);
      if (product == null) {
        // If the product doesn't exist, we return a 404 error.
        response.sendErrorMessage(404, "Product not found");
        return response;
      }
      // Check if the product is out of stock.
      if (product.getStock() <= 0) {
        // If the product is out of stock, we return a 400 error.
        response.sendErrorMessage(400, "Product is out of stock");
        return response;
      }
      // Check if the user is trying to add more than the product has in stock.
      if (product.getStock() - cartItemsDto.getQuantity() < 0) {
        // If the user is trying to add more than the product has in stock,
        // we return a 400 error.
        response.sendErrorMessage(400, "Please try again with an lower stock");
        return response;
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
      response.sendSuccessResponse(200, "Cart will be updated shortly");
      return response;
    } catch (Exception e) {
      // If there is an error while processing the request, we return a 500 error with
      // the message.
      response.sendErrorMessage(500, e.getMessage());
      return response;
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public ReqRes removeProductFromCart(HttpServletRequest httpServletRequest) throws Exception {
    ReqRes response = new ReqRes();

    // Extract the user's ID from the request.
    Long id = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
    // Find the user's cart.
    Cart cart = cartRepo.findByUser(id);
    // If the cart is empty, return a 400 error.
    if (cart.getCartItems().size() == 0) {
      throw new Exception("No Products found to remove");
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
    response.sendSuccessResponse(200, "Successfully removed");
    return response;

  }

  @Transactional
  public ReqRes decrementCartQuantity(HttpServletRequest httpServletRequest, DecrementCartQuantityDto cartQuantityDto)
      throws Exception {
    ReqRes response = new ReqRes();
    try {
      // Get the user ID from the request.
      long userId = jwtInterceptor.getIdFromJwt(httpServletRequest);

      // Find the user's cart.
      Cart cart = cartRepo.findByUser(userId);

      // If the cart does not exist, return a 401 error.
      if (cart == null || cart.getCartItems().isEmpty()) {
        response.sendErrorMessage(401, "We are having some issue");
        return response;
      }

      // Stream the cart items and check if a product matches the ID we are looking
      // for.
      // If a match is found, decrement the quantity and update the cart total.
      // Save the updated cart.
      boolean isCartUpdated = cart.getCartItems().stream()
          .filter(pro -> pro.getProduct().getId() == cartQuantityDto.getProductId()).findFirst().map(p -> {
            p.setQuantity(p.getQuantity() - 1);
            cart.setCartTotal(cart.getCartTotal() - p.getProduct().getPrice());
            cartRepo.save(cart);
            return true;
          }).orElse(false);

      // If the cart was updated, return a 200 success response with the updated cart.
      // Otherwise, return a 404 error.
      if (isCartUpdated) {
        response.sendSuccessResponse(200, "Quantity decrement successfully done!", cart);
        return response;
      }
      response.sendErrorMessage(404, "Product not found to decrement the quantity");
      return response;

    } catch (Exception e) {
      // If there is an error, return a 500 error with the error message.
      response.sendErrorMessage(500, e.getMessage());
      throw new Exception(e.getMessage());
    }
  }

  public ReqRes getCartInfo(HttpServletRequest httpServletRequest) {
    ReqRes response = new ReqRes();
    try {
      // Get the user's ID from the JWT token in the HTTP request.
      Long id = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);

      // Check if the user's cart is in the redis cache.
      // If the cart is in the cache, return it to the user.
      Object CacheCartInfo = redisService.getData("CART" + id, Object.class);
      if (CacheCartInfo != null) {
        response.sendSuccessResponse(200, "Cart Products found from redis", CacheCartInfo);
        return response;
      }

      // If the cart is not in the cache, find the user's cart in the database.
      Cart cartinfo = cartRepo.findByUser(id);

      // If the cart does not exist in the database, or if it is empty, return a 400
      // error.
      if (cartinfo == null || cartinfo.getCartItems().isEmpty() || cartinfo.getCartItems().size() == 0) {
        response.setMessage("Empty Cart");
        response.setStatusCode(400);
        return response;
      }

      // Save the user's cart in the redis cache.
      redisService.saveInSingleQuery("CART" + id, cartinfo.getCartItems(), 20);

      // Return the user's cart.
      response.sendSuccessResponse(200, "Cart Products found from redis", cartinfo.getCartItems());
      return response;

    } catch (Exception e) {
      // If there is an error, return a 500 error with the error message.
      response.sendErrorMessage(500, e.getMessage());
      response.setStatusCode(500);
      return response;
    }
  }

  public Boolean updateCart(ProductParseDto valueObject) {
    if (valueObject != null) {
      // Get the user ID from the object.
      long user = (long) valueObject.getUser();

      // Find the user's cart.
      Cart cart = cartRepo.findByUser(user);

      // If the cart doesn't exist, or if it is empty, return.
      if (cart == null || cart.getCartItems() == null) {
        return false;
      }

      // Check if the product is already in the user's cart.
      boolean isProductAlredySave = cart.getCartItems().stream().
      // Filter the cart items to find the product with the same ID as the one in the
      // value object.
          filter(pro -> pro.getProduct().getId() == valueObject.getProductId()).
          // If the product exists, update the quantity and the cart total.
          findFirst().map(p -> {
            // Increment the quantity of the product by the quantity in the value object.
            p.setQuantity(p.getQuantity() + valueObject.getQuantity());
            // Update the cart total.
            float total = cart.getCartTotal() + (valueObject.getQuantity() * p.getProduct().getPrice());
            cart.setCartTotal(total);
            // Update the discounted cart total.
            cart.setDiscountCartTotal(total);
            // Return true to indicate that the product was found and updated.
            return false;
          }).orElse(false);

      // If the product was found and updated, save the cart and return.
      if (isProductAlredySave) {
        cartRepo.save(cart);
        return false;
      }

      // If the product was not found, find the product in the database.
      Products product = productRepo.findById(valueObject.getProductId()).orElse(null);

      // If the product doesn't exist, or if its stock is 0, return.
      if (product == null || product.getStock() <= 0) {
        return false;
      }

      // Create a new cart item and set its product and cart.
      CartItems cartItems = new CartItems();
      cartItems.setProduct(product);
      cartItems.setCart(cart);

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
      float total = cartItems.getQuantity() * cartItems.getProduct().getPrice();
      cart.setCartTotal(total);
      cart.setDiscountCartTotal(total);

      // Save the cart.
      cartRepo.save(cart);
      return true;
    }
    return false;
  }

}