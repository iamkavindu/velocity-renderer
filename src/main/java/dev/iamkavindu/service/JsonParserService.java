package dev.iamkavindu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of JsonParserService using Jackson ObjectMapper.
 * Handles parsing of JSON strings into Map structures with proper error handling.
 */
@Service
public class JsonParserService {
    
    private final ObjectMapper objectMapper;
    
    public JsonParserService() {
        objectMapper = new ObjectMapper();
    }

    public Map<String, Object> parseJsonToMap(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return objectMapper.readValue(jsonString, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
    }
}

