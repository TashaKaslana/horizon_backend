package org.phong.horizon.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {
    // You might have other web configurations here (e.g., customizing argument resolvers)
    // Often, just the annotation is enough if you're using Spring Boot defaults.
}