package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 04:43
 **/
public class EnumKeyDeserializer<T> extends KeyDeserializer {

	private final EnumMapper<T> enumMapper;

	public EnumKeyDeserializer(EnumMapper<T> enumMapper) {
		this.enumMapper = enumMapper;
	}

	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
		Object enumObject = this.enumMapper.getEnum(key);
		if (enumObject == null) {
			throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", key));
		}
		return enumObject;
	}

}
