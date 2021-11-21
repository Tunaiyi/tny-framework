package com.tny.game.basics.configuration;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.mongodb.mapper.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.configuration.*;
import com.tny.game.data.mongodb.loader.*;
import com.tny.game.data.mongodb.mapper.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@ConditionalOnClass(MongodbAutoConfiguration.class)
@EnableConfigurationProperties({
		DefaultItemModelProperties.class,
})
public class BasicsMongoAutoConfiguration {

	@Bean
	ItemModelJsonSerializer itemModelJsonSerializer() {
		return new ItemModelJsonSerializer();
	}

	@Bean
	@ConditionalOnBean(GameExplorer.class)
	ItemModelJsonDeserializer itemModelJsonDeserializer(GameExplorer gameExplorer) {
		return new ItemModelJsonDeserializer(gameExplorer);
	}

	@Bean
	@ConditionalOnMissingBean(EntityLoadedService.class)
	EntityLoadedService defaultEntityLoadedService(ApplicationContext applicationContext) {
		return new DefaultEntityLoadedService(applicationContext);
	}

	@Bean
	GameJsonEntityObjectConverter gameJsonEntityObjectConverter(
			EntityLoadedService entityOnLoadService,
			ObjectProvider<JsonEntityConverterMapperCustomizer> mapperCustomizers) {
		ObjectMapper mapper = ObjectMapperFactory.createMapper();
		mapper.registerModule(MongoObjectMapperMixLoader.getModule())
				.setAnnotationIntrospector(new MongoIdIntrospector())
				.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		mapperCustomizers.forEach((action) -> action.customize(mapper));
		return new GameJsonEntityObjectConverter(entityOnLoadService, mapper);
	}

	@Bean
	ItemModelEntityConverterMapperCustomizer itemModelEntityConverterMapperCustomizer(
			ItemModelJsonSerializer serializer, ItemModelJsonDeserializer deserializer) {
		return new ItemModelEntityConverterMapperCustomizer(serializer, deserializer);
	}

}
