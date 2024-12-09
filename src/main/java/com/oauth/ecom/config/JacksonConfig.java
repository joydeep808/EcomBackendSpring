// package com.oauth.ecom.config;

// import java.time.format.DateTimeFormatter;

// import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
// import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
// import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
// import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

// @Configuration
// public class JacksonConfig {

    
//     @Bean
//     public JavaTimeModule javaTimeModule() {
//         return new JavaTimeModule();
//     }
//     @Bean
//     public ObjectMapper objectMapper() {
//         ObjectMapper mapper = new ObjectMapper();
//         JavaTimeModule module = new JavaTimeModule();
//         mapper.registerModule(module);
//         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // This is fine for Java 8 LocalDate serialization
//         mapper.findAndRegisterModules();  // Automatically register any other modules
//         return mapper;
//     }

//     @Bean
//     public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
//         MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
//         return converter;
//     }


//     @Bean
//     public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {

//         return builder -> {

//             // formatter
//             DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//             DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//             // deserializers
//             builder.deserializers(new LocalDateDeserializer(dateFormatter));
//             builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));

//             // serializers
//             builder.serializers(new LocalDateSerializer(dateFormatter));
//             builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
//         };
//     }
// }
