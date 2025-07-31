package com.bobocode;

import org.springframework.context.annotation.Bean;

/**
 * Configuration class that registers {@link TrimmedAnnotationBeanPostProcessor} manually.
 */
public class StringTrimmingConfiguration {

    @Bean
    public TrimmedAnnotationBeanPostProcessor trimmedAnnotationBeanPostProcessor() {
        return new TrimmedAnnotationBeanPostProcessor();
    }
}
