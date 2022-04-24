package com.tny.game.boot.utils;

import com.google.common.collect.*;
import com.tny.game.common.utils.*;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-04 02:51
 */
public final class SpringBeanUtils {

    private SpringBeanUtils() {
    }

    public static <K, B> Map<K, B> beanMapOfType(ApplicationContext applicationContext,
            Class<B> beanClass, BiFunction<String/* Bean Name*/, B/* Bean */, K /* Key */> keyGetter) {
        Map<K, B> beanMap = new HashMap<>();
        beansStream(applicationContext, beanClass)
                .forEach(bean -> {
                    K key = keyGetter.apply(bean.getKey(), bean.getValue());
                    B old = beanMap.putIfAbsent(key, bean.getValue());
                    Asserts.checkArgument(old == null, "{} load bean exception, {} and {} have the same key {}", beanClass, bean, old, key);
                });
        return ImmutableMap.copyOf(beanMap);
    }

    public static <K, B> Map<K, B> beanMapOfType(ApplicationContext applicationContext, Class<B> beanClass, Function<B, K> keyGetter) {
        Map<K, B> beanMap = new HashMap<>();
        beansStream(applicationContext, beanClass)
                .map(Entry::getValue)
                .forEach(bean -> {
                    K key = keyGetter.apply(bean);
                    B old = beanMap.putIfAbsent(key, bean);
                    Asserts.checkArgument(old == null, "{} load bean exception, {} and {} have the same key {}", beanClass, bean, old, key);
                });
        return ImmutableMap.copyOf(beanMap);
    }

    public static <B> Set<B> beansOfType(ApplicationContext applicationContext, Class<B> beanClass) {
        return ImmutableSet.copyOf(beansStream(applicationContext, beanClass)
                .map(Entry::getValue)
                .collect(Collectors.toSet()));
    }

    private static <S> Stream<Entry<String, S>> beansStream(ApplicationContext applicationContext, Class<S> beanClass) {
        Map<String, S> beans = applicationContext.getBeansOfType(beanClass);
        return beans.entrySet()
                .stream()
                .filter(e -> !e.getKey().startsWith("scopedTarget."));
    }

}
