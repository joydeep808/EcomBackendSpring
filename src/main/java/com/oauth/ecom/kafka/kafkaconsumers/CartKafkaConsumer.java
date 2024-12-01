// package com.oauth.ecom.kafka.kafkaconsumers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.oauth.ecom.dto.product.ProductParseDto;
// import com.oauth.ecom.entity.Cart;
// import com.oauth.ecom.entity.CartItems;
// import com.oauth.ecom.entity.Products;
// import com.oauth.ecom.repository.CartItemsRepo;
// import com.oauth.ecom.repository.CartRepo;
// import com.oauth.ecom.repository.ProductRepo;


// @Service
// public class CartKafkaConsumer {

//   @Autowired(required = true)
//   private CartItemsRepo cartItemsRepo;
//   @Autowired(required = true)
//   private CartRepo cartRepo;
//   @Autowired(required = true)
//   private ProductRepo productRepo;


//   private final ObjectMapper objectMapper = new ObjectMapper();
//   @KafkaListener(topics = "cart_topic" ,groupId = "ecom_group")
//   @Transactional
//   public void kafkaListner(String  message) throws Exception {
//     try {
//     ProductParseDto valueObject =  objectMapper.readValue(message, ProductParseDto.class);
//     if (valueObject != null) {
//      long user =(long) valueObject.getUser();
//      Cart cart = cartRepo.findByUser(user);
//      if (cart == null || cart.getCartItems() == null) {
//       return;
//      }
//      boolean isProductAlredySave  = cart.getCartItems().stream().
//      filter(pro-> pro.getProduct().getId() == valueObject.getProductId()).
//       findFirst().map(p-> {
//       p.setQuantity(p.getQuantity() + valueObject.getQuantity());
//       float total = cart.getCartTotal() + (valueObject.getQuantity() * p.getProduct().getPrice());
//       cart.setCartTotal(total);
//       cart.setDiscountCartTotal(total);
//      return true;
//     }).orElse(false);
//      if (isProductAlredySave) {
      
//        cartRepo.save(cart);
//        return;
//      }
//      Products product = productRepo.findById(valueObject.getProductId()).orElse(null);
//      if (product.getStock() <= 0) {
//       return;
//      }
//       CartItems cartItems = new CartItems();
//       cartItems.setProduct(product);
//       cartItems.setCart(cart);
//       if (product.getStock() - valueObject.getQuantity() < 0 ) {
//         cartItems.setQuantity(1);
//         cartItemsRepo.saveAndFlush(cartItems);
//         float total = valueObject.getQuantity() * 1;
//         cart.setCartTotal(total);
//         cart.setDiscountCartTotal(total);
//         cartRepo.save(cart);
//         return;
//       }
//       float total = valueObject.getQuantity() * product.getPrice();
//       cart.setCartTotal(total);
//       cart.setDiscountCartTotal(total);
//       cartRepo.save(cart);
//       cartItems.setQuantity(valueObject.getQuantity());
//       cartItemsRepo.saveAndFlush(cartItems);
//       return;
//     }
//      throw new Exception("error");
//     } catch (Exception e) {
//      System.out.println(e.getLocalizedMessage());
//      throw new Exception(e.getMessage());
//     }
//   }
// }



