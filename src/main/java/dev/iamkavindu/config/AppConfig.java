package dev.iamkavindu.config;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine engine = new VelocityEngine();

        engine.setProperty("resource.loaders", "string");
        engine.setProperty("resource.loader.string.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        engine.setProperty("runtime.log.name", "velocity");
        engine.setProperty("parser.pool.size", "20");

        engine.init();
        return engine;
    }
}
