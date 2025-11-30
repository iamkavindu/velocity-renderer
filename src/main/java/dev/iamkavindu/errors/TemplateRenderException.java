package dev.iamkavindu.errors;

/**
 * Custom exception for template rendering errors.
 * Provides specific exception type for template processing failures,
 * making error handling more explicit and type-safe.
 */
public class TemplateRenderException extends Exception {
    
    /**
     * Constructs a new TemplateRenderException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the error
     * @param cause the underlying cause of the exception
     */
    public TemplateRenderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new TemplateRenderException with the specified detail message.
     * 
     * @param message the detail message explaining the error
     */
    public TemplateRenderException(String message) {
        super(message);
    }
}

