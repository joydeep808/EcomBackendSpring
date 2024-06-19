package com.oauth.ecom.services.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class RedisService {
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  @Autowired
  private RedisTemplate<String , Object> redisTemplate;
  public RedisService(){
    
  }
  
  public boolean saveData(String key , Object value){
    try {
      
      redisTemplate.opsForValue().set( key, objectMapper.writeValueAsString(value));
      redisTemplate.expire(key, Duration.ofSeconds(60));
      return true;
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
      return false;
    }
  }
  public boolean saveInSingleQuery(String key , Object value , int expiration){
    try {
      objectMapper.registerModule(new JavaTimeModule());
      String JsonToString = objectMapper.writeValueAsString(value);
      redisTemplate.execute(new SessionCallback<Object>(){
        @SuppressWarnings("unchecked")
        @Override
        public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations){
          operations.opsForValue().set(key, JsonToString);
          operations.expire(key, Duration.ofSeconds(expiration));
          return null;
        }
      });
      return true;
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
      return false;
    }

  }
  public boolean saveCouponCode(String couponCode){
    try {
      redisTemplate.execute(new SessionCallback<Object> (){
        @SuppressWarnings("unchecked")
        @Override
        public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations){
          operations.opsForValue().set("couponCode"+couponCode, couponCode);
          operations.expire("couponCode"+couponCode, Duration.ofMinutes(3));
          return null;
        }
      });
      return true;
    } catch (Exception e) {
      return false;
    }
  }


  public <T> T getData(String key  ,Class<T> classType) throws JsonMappingException, JsonProcessingException{
    String values = (String) redisTemplate.opsForValue().get(key);
    if (values == null) {
      return null;
    }
   return objectMapper.readValue(values , classType);
  
  }
  public String getCouponData(String key ) throws JsonMappingException, JsonProcessingException{
    String values = (String) redisTemplate.opsForValue().get(key);
    if (values == null) {
      return null;
    }
   return values;
  
  }


  // Coupon Code set 


  
}
