package com.tny.game.basics.mongodb.mapper;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.data.mongodb.loader.*;
import com.tny.game.data.mongodb.mapper.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 12:17 下午
 */
public class MongoBasicsObjectMapperCustomizer implements ObjectMapperCustomizer {

	public MongoBasicsObjectMapperCustomizer() {
	}

	@Override
	public void customize(ObjectMapper mapper) {
		mapper.registerModule(MongoObjectMapperMixLoader.getModule())
				.setAnnotationIntrospector(new MongoIdIntrospector())
				.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
	}

}
