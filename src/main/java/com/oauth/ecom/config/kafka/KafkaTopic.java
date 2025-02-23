// package com.oauth.ecom.config.kafka;

// import java.util.HashMap;
// import java.util.Map;

// import org.apache.kafka.clients.admin.NewTopic;
// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.config.TopicBuilder;

// import com.fasterxml.jackson.databind.ser.std.StringSerializer;

// @Configuration
// @EnableKafka
// public class KafkaTopic {
//   @Value("${spring.kafka.bootstrap-servers}")
//   private String kafkaConnectinoString;
  
// @Bean
// public Map<String , Object> producerConfigs(){
//   Map<String , Object> props  = new  HashMap<>();
//   props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConnectinoString);
//   props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//   props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  
//   return props;
  
// }

//   @Bean
//   public NewTopic orderTopic(){
//     return TopicBuilder.name("order_topic").build();
//   }
//   @Bean 
//   public NewTopic cartTopic(){
//     return TopicBuilder.name("cart_topic").build();
//   }
//   @Bean
//   public NewTopic couponCodeTopic(){
//     return TopicBuilder.name("coupon_Code_Topic").build();
//   }
//   @Bean
//   public NewTopic welcomEmailSend(){
//     return TopicBuilder.name("welcome_Email_Send").build();
//   }
// }
