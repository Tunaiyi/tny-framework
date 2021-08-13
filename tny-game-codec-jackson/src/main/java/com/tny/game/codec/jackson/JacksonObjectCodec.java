package com.tny.game.codec.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.tny.game.codec.*;

import java.io.*;
import java.lang.reflect.Type;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/9 5:46 下午
 */
public class JacksonObjectCodec<T> implements ObjectCodec<T> {

	private final ObjectMapper objectMapper;

	private final JavaType javaType;

	public JacksonObjectCodec(Type clazz, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.javaType = this.objectMapper.getTypeFactory().constructType(clazz);
	}

	@Override
	public boolean isPlaintext() {
		return true;
	}

	@Override
	public byte[] encode(T value) throws IOException {
		try {
			return this.objectMapper.writeValueAsBytes(value);
		} catch (JsonProcessingException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void encode(T value, OutputStream output) throws IOException {
		try {
			this.objectMapper.writeValue(output, value);
		} catch (JsonProcessingException e) {
			throw new IOException(e);
		}
	}

	@Override
	public T decode(byte[] bytes) throws IOException {
		return this.objectMapper.readValue(bytes, this.javaType);
	}

	@Override
	public T decode(InputStream input) throws IOException {
		return this.objectMapper.readValue(input, this.javaType);
	}

}
