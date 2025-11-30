package dev.iamkavindu.service;

import java.util.Map;

/**
 * Service interface for JSON parsing operations.
 * Provides contract for converting JSON strings into Map structures
 * suitable for template rendering contexts.
 */
public interface JsonParserService {
    
    /**
     * Parse JSON string into a Map for Velocity context
     * 
     * @param jsonString JSON formatted string
     * @return Map of key-value pairs representing the JSON structure
     * @throws IllegalArgumentException if JSON is invalid or cannot be parsed
     */
    Map<String, Object> parseJsonToMap(String jsonString);
}

