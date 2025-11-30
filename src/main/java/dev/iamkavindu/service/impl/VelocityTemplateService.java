package dev.iamkavindu.service.impl;

import dev.iamkavindu.errors.TemplateRenderException;
import dev.iamkavindu.service.TemplateRenderService;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

/**
 * Apache Velocity implementation of TemplateRenderService.
 * Initializes VelocityEngine once as a singleton Spring bean (DRY principle).
 * Configured for string-based template processing (not file-based).
 */
@Service
public class VelocityTemplateService implements TemplateRenderService {
    
    private final VelocityEngine velocityEngine;

    public VelocityTemplateService(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
    
    @Override
    public String render(String template, Map<String, Object> context) throws TemplateRenderException {
        if (template == null) {
            throw new TemplateRenderException("Template cannot be null");
        }
        
        if (context == null) {
            throw new TemplateRenderException("Context cannot be null");
        }
        
        try {
            VelocityContext velocityContext = new VelocityContext();
            context.forEach(velocityContext::put);

            StringWriter writer = new StringWriter();

            boolean success = velocityEngine.evaluate(
                velocityContext, 
                writer, 
                "TemplateRenderer", 
                template
            );
            
            if (!success) {
                throw new TemplateRenderException("Template evaluation failed");
            }
            return writer.toString();
        } catch (Exception e) {
            throw new TemplateRenderException("Failed to render template: " + e.getMessage(), e);
        }
    }
}
