package com.bobocode.annotation;

import com.bobocode.StringTrimmingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation that can be placed on configuration class to import {@link StringTrimmingConfiguration}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(StringTrimmingConfiguration.class)
public @interface EnableStringTrimming {
//todo: Implement EnableStringTrimming annotation according to javadoc
}
