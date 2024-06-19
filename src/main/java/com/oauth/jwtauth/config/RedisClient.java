package com.oauth.jwtauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@EnableRedisRepositories
public class RedisClient {

  @Autowired
  private RedisProperties redisProperties;
  // @Value("${spring.data.redis.host}")
  // private String redishost;
  // @Value("${spring.data.redis.port}")
  // private int port;
  // @Value("${spring.data.redis.password}")
  // private String password;


  // @Bean
  // public JedisConnectionFactory jedisConnectionFactory() {
  //     RedisStandaloneConfiguration standaloneConfiguration =  new RedisStandaloneConfiguration();
  //     System.out.println(redisProperties.getHost() + redisProperties.getPassword());
  //     standaloneConfiguration.setHostName(redishost);
  //     standaloneConfiguration.setPort(port);
  //     standaloneConfiguration.setPassword(password);
  //     return new JedisConnectionFactory(standaloneConfiguration);
  // }
  @Bean
  public RedisConnectionFactory connectionFactory(){
    RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
    standaloneConfiguration.setHostName(redisProperties.getHost());
    standaloneConfiguration.setPort(redisProperties.getPort());
    standaloneConfiguration.setPassword(redisProperties.getPassword());
    return new LettuceConnectionFactory(standaloneConfiguration);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
      RedisTemplate<String, Object> template = new RedisTemplate<>();
      template.setConnectionFactory(connectionFactory());
      template.setKeySerializer(new StringRedisSerializer());
      template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
      // template.setHashKeySerializer(new JdkSerializationRedisSerializer());
      // template.setValueSerializer(new JdkSerializationRedisSerializer());
      template.setEnableTransactionSupport(true);
      template.afterPropertiesSet();
      return template;
  }
}
