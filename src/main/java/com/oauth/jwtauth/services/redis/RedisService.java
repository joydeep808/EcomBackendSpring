package com.oauth.jwtauth.services.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
  @Autowired
  private RedisTemplate<String , Object> redisTemplate;
  public boolean saveData(String key , Object value){
    try {
      redisTemplate.opsForValue().set( key, value);
      redisTemplate.expire(key, Duration.ofSeconds(60));
      return true;
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
      return false;
    }
  }
  public boolean saveInSingleQuery(String key , Object value , int expiration){
    try {
      redisTemplate.execute(new SessionCallback<Object>(){
        @Override
        public Object execute(RedisOperations operations){
          operations.opsForValue().set(key, value);
          operations.expire(key, Duration.ofSeconds(expiration));
          return null;
        }
      });
      return true;
    } catch (Exception e) {
      return false;
    }

  }
  public Object getData(String key){
    return  redisTemplate.opsForValue().get(key);
  }
}
