package com.tny.game.data.configuration.mongodb;

import com.tny.game.data.configuration.*;
import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.configuration.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration
@ConditionalOnClass(MongodbStorageAccessorFactory.class)
@AutoConfigureAfter({MongodbAutoConfiguration.class})
@AutoConfigureBefore(GameDataAutoConfiguration.class)
@Import({
		ImportMongodbStorageAccessorFactoryDefinitionRegistrar.class
})
public class MongodbStorageAutoConfiguration {

	@Bean
	@ConditionalOnClass(EntityIdConverter.class)
	public EntityIdConverter<?, ?, ?> defaultEntityIdConverter() {
		return new DefaultEntityIdConverter<>();
	}

}
