package dev.iamkavindu.service;

import dev.iamkavindu.errors.TemplateRenderException;

import java.util.Map;

/**
 * Service interface for template rendering operations.
 * Defines contract for rendering templates with variable substitution.
 * Follows Open/Closed Principle - open for extension (new template engines),
 * closed for modification.
 */
public interface TemplateRenderService {
    
    /**
     * Render template with provided context variables
     * 
     * @param template Template string with engine-specific syntax
     * @param context Map of variables to merge into the template
     * @return Rendered HTML string with variables substituted
     * @throws TemplateRenderException if rendering fails due to syntax errors or other issues
     */
    String render(String template, Map<String, Object> context) throws TemplateRenderException;
}

