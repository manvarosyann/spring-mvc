package com.bobocode;

import com.bobocode.annotation.EnableStringTrimming;
import com.bobocode.annotation.Trimmed;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.IntStream;

/**
 * This processor class implements {@link BeanPostProcessor}, looks for beans where method parameters are marked with
 * {@link Trimmed} annotation, creates proxies of them, overrides methods, and trims all {@link String} arguments marked with
 * {@link Trimmed}. For example, if there is a string " Java   " as an input parameter, it will automatically be trimmed to "Java"
 * if the parameter is marked with {@link Trimmed} annotation.
 * <p>
 * Note: This bean is not marked as a {@link Component} to avoid automatic scanning; instead, it should be created in
 * {@link StringTrimmingConfiguration} class which can be imported into a {@link Configuration} class via the
 * {@link EnableStringTrimming} annotation.
 */
public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        boolean hasTrimmedParam = false;
        for (Method method : targetClass.getMethods()) {
            for (Parameter parameter : method.getParameters()) {
                if (parameter.isAnnotationPresent(Trimmed.class) && parameter.getType() == String.class) {
                    hasTrimmedParam = true;
                    break;
                }
            }
            if (hasTrimmedParam) break;
        }

        if (!hasTrimmedParam) {
            return bean;
        }

        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice((MethodInterceptor) invocation -> {
            Object[] args = invocation.getArguments();
            Method method = invocation.getMethod();
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
        });

        return proxyFactory.getProxy();
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
