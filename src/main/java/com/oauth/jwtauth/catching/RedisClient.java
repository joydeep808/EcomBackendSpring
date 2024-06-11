package com.oauth.jwtauth.catching;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisClient {

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
      RedisStandaloneConfiguration standaloneConfiguration =  new RedisStandaloneConfiguration();
      standaloneConfiguration.setHostName("redis-16643.c264.ap-south-1-1.ec2.redns.redis-cloud.com");
      standaloneConfiguration.setPort(16643);
      standaloneConfiguration.setPassword("eDcJwpLoqHMsRyKseeFDKv8krpcFlXa6");
      // standaloneConfiguration.setHostName("localhost");
      // standaloneConfiguration.setPort(6379);
      return new JedisConnectionFactory(standaloneConfiguration);
  }
  @Bean
  public RedisConnectionFactory connectionFactory(){
    return new LettuceConnectionFactory();
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
