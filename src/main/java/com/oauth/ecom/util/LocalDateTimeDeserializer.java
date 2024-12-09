package com.oauth.ecom.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDate> {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    return LocalDate.parse(p.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
  }

  // @Override
  // public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws  java.io.IOException {
  //     String dateString = jsonParser.getValueAsString();
  //     LocalDate localDate = LocalDate.parse(dateString, FORMATTER);
  //     return localDate.atStartOfDay();
  // }
}