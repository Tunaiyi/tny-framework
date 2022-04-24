package com.tny.game.data.mongodb.configuration;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.mongodb.MongoClientSettings;
import com.tny.game.boot.utils.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.loader.*;
import com.tny.game.data.mongodb.mapper.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>ConfigurationPropertiesBindingPostProcessor
 */
@Configuration(proxyBeanMethods = false)
public class MongodbBeanConfiguration {

    @Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder().build();
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions(ApplicationContext applicationContext) {
        Collection<Converter<?, ?>> converterBeans = as(SpringBeanUtils.beansOfType(applicationContext, Converter.class));
        Collection<Converter<?, ?>> converters = new ArrayList<>(converterBeans);
        return MongoCustomConversions.create((adapter) -> adapter.registerConverters(converters));
    }

    @Bean
    public MongoEntityClasses mongoEntityClasses(ApplicationContext applicationContext) throws ClassNotFoundException {
        Set<Class<?>> classes = new EntityScanner(applicationContext).scan(Document.class, Persistent.class);
        return new MongoEntityClasses(classes);
    }

    @Bean
    @ConditionalOnMissingBean(MongoObjectLoadedService.class)
    public MongoObjectLoadedService defaultEntityLoadedService(ApplicationContext applicationContext) {
        return new DefaultMongoObjectLoadedService(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean(value = MongoDocumentConverter.class)
    public MongoDocumentConverter jsonMongoDocumentMapper(
            ObjectProvider<MongoDocumentEnhance<?>> enhances,
            ObjectProvider<ObjectMapperCustomizer> mapperCustomizers) {
        ObjectMapper mapper = ObjectMapperFactory.createMapper();
        mapper.registerModule(MongoObjectMapperMixLoader.getModule())
                .setAnnotationIntrospector(new MongoIdIntrospector())
                .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        mapperCustomizers.forEach((action) -> action.customize(mapper));
        return new JsonMongoDocumentConverter(mapper, enhances.stream().collect(Collectors.toList()));
    }

}
