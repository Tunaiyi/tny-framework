package com.tny.game.data.mongodb.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * <p>ConfigurationPropertiesBindingPostProcessor
 */
@Configuration
@Import({
		ImportConverterBeanDefinitionRegistrar.class,
		MongodbBeanConfiguration.class,
		ImportMongodbDataSourceBeanDefinitionRegistrar.class
})
@EnableConfigurationProperties(MongodbDataSourceProperties.class)
public class MongodbAutoConfiguration {

}
