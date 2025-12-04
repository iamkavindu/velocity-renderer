package dev.iamkavindu.errors;

/**
 * Custom exception for template rendering errors.
 * Provides specific exception type for template processing failures,
 * making error handling more explicit and type-safe.
 */
public class TemplateRenderException extends Exception {

    public TemplateRenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateRenderException(String message) {
        super(message);
    }
}
