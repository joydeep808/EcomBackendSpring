package com.oauth.ecom.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws  java.io.IOException {
      String dateString = jsonParser.getValueAsString();
      LocalDate localDate = LocalDate.parse(dateString, FORMATTER);
      return localDate.atStartOfDay();
  }
}