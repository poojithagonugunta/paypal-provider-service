package com.hulkhiretech.payments.utils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtils {

	private final ObjectMapper mapper;
	
	public String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			log.error("Error converting object to JSON", e);
			throw new RuntimeException("JSON conversion error: " + e.getMessage());
		}
	}
	
	public <T> T fromJson(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (Exception e) {
			log.error("Error converting JSON to object", e);
			throw new RuntimeException("JSON conversion error: " + e.getMessage());
		}
	}

}