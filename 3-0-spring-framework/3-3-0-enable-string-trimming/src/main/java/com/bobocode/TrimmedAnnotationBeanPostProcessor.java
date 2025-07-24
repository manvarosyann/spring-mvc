package com.bobocode;

import com.bobocode.annotation.EnableStringTrimming;
import com.bobocode.annotation.Trimmed;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.IntStream;

/**
 * This is processor class implements {@link BeanPostProcessor}, looks for a beans where method parameters are marked with
 * {@link Trimmed} annotation, creates proxy of them, overrides methods and trims all {@link String} arguments marked with
 * {@link Trimmed}. For example if there is a string " Java   " as an input parameter it has to be automatically trimmed to "Java"
 * if parameter is marked with {@link Trimmed} annotation.
 * <p>
 * <p>
 * Note! This bean is not marked as a {@link Component} to avoid automatic scanning, instead it should be created in
 * {@link StringTrimmingConfiguration} class which can be imported to a {@link Configuration} class by annotation
 * {@link EnableStringTrimming}
 */
public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> targetClass = bean.getClass();

        boolean hasTrimmedParam = false;

        // Check if any method has at least one parameter annotated with @Trimmed
        for (Method method : targetClass.getMethods()) {
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(Trimmed.class) && parameter.getType() == String.class) {
                    hasTrimmedParam = true;
                    break;
                }
            }
        }

        if (!hasTrimmedParam) {
            return bean; // No need to proxy
        }

        // Create a proxy that trims @Trimmed String parameters
        return Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                (proxy, method, args) -> {
                    if (args != null) {
                        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                        Class<?>[] parameterTypes = method.getParameterTypes();

                        Object[] modifiedArgs = new Object[args.length];
                        IntStream.range(0, args.length).forEach(i -> {
                            if (parameterTypes[i] == String.class &&
                                    isAnnotatedWith(parameterAnnotations[i], Trimmed.class) &&
                                    args[i] != null) {
                                modifiedArgs[i] = ((String) args[i]).trim();
                            } else {
                                modifiedArgs[i] = args[i];
                            }
                        });
                        return method.invoke(bean, modifiedArgs);
                    }
                    return method.invoke(bean);
                }
        );
    }

    private boolean isAnnotatedWith(Annotation[] annotations, Class<? extends Annotation> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                return true;
            }
        }
        return false;
    }
}
