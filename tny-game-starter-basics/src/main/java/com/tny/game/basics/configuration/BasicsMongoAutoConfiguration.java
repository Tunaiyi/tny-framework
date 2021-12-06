package com.tny.game.basics.configuration;

import com.tny.game.basics.mongodb.mapper.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.configuration.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MongodbAutoConfiguration.class)
@EnableConfigurationProperties({
		DefaultItemModelProperties.class,
})
public class BasicsMongoAutoConfiguration {

	@Bean
	MongoBasicsObjectMapperCustomizer mongoBasicsObjectMapperCustomizer() {
		return new MongoBasicsObjectMapperCustomizer();
	}

	@Bean
	GameJsonMongoEntityConverter gameJsonEntityObjectConverter(
			ObjectProvider<MongoDocumentEnhance<?>> enhances,
			ObjectProvider<ObjectMapperCustomizer> mapperCustomizers) {
		ObjectMapperFactory factory = new ObjectMapperFactory();
		factory.addCustomizers(mapperCustomizers.stream().collect(Collectors.toList()));
		return new GameJsonMongoEntityConverter(factory.create(), enhances.stream().collect(Collectors.toList()));
	}

}
