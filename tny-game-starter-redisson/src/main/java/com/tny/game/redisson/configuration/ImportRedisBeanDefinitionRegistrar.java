package com.tny.game.redisson.configuration;

import com.tny.game.codec.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.redisson.*;
import com.tny.game.redisson.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * <p>
 */
public abstract class ImportRedisBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata annotationMetadata, @Nonnull BeanDefinitionRegistry registry) {
        for (Class<?> entityClass : RedisObjectClassLoader.getAllClasses()) {
            RedisObject source = entityClass.getAnnotation(RedisObject.class);
            if (source == null) {
                continue;
            }
            register(registry, entityClass, source);
        }
        for (RedisObjectRegistrar registrar : RedisObjectClassLoader.getAllRegistrars()) {
            register(registry, registrar.value(), registrar.object());
        }
    }

    private void register(BeanDefinitionRegistry registry, Class<?> entityClass, RedisObject source) {
        Codecable codecable = source.codec();
        String mimeType = MimeTypeAide.getMimeType(codecable);
        if (Objects.equals(mimeType, MimeTypeAide.NONE)) {
            codecable = entityClass.getAnnotation(Codecable.class);
            Asserts.checkNotNull(codecable, "{} is not {} annotation", entityClass, Codecable.class);
            mimeType = MimeTypeAide.getMimeType(codecable);
        }
        Asserts.checkArgument(StringUtils.isNotBlank(mimeType), "{} mimeType must not blank {} ", entityClass, mimeType);
        doRegister(registry, entityClass, mimeType, source.source());
    }

    protected abstract <T> void doRegister(BeanDefinitionRegistry registry, Class<T> entityClass, String mimeType, String source);

}
