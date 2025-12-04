package dev.iamkavindu.service;

import dev.iamkavindu.errors.TemplateRenderException;
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
public class VelocityTemplateService {

    private final VelocityEngine velocityEngine;
    private final VelocityContext velocityContext;

    public VelocityTemplateService(VelocityEngine velocityEngine, VelocityContext velocityContext) {
        this.velocityEngine = velocityEngine;
        this.velocityContext = velocityContext;
    }

    public String render(String template, Map<String, Object> context) throws TemplateRenderException {
        if (template == null) {
            throw new TemplateRenderException("Template cannot be null");
        }

        if (context == null) {
            throw new TemplateRenderException("Context cannot be null");
        }

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
    }
}
