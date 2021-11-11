package com.tny.game.basics.mongodb.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.basics.item.*;
import com.tny.game.basics.loader.*;
import com.tny.game.data.mongodb.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 12:17 下午
 */
public class ItemModelEntityConverterMapperCustomizer implements JsonEntityConverterMapperCustomizer {

	private final ItemModelJsonSerializer serializer;

	private final ItemModelJsonDeserializer deserializer;

	public ItemModelEntityConverterMapperCustomizer(
			ItemModelJsonSerializer serializer, ItemModelJsonDeserializer deserializer) {
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public void customize(ObjectMapper mapper) {
		SimpleModule module = new SimpleModule();
		for (Class<? extends ItemModel> modelClass : ItemModelClassLoader.getClasses()) {
			module.addSerializer(modelClass, serializer);
			module.addDeserializer(as(modelClass), deserializer);
		}
		mapper.registerModule(module);
	}

}
