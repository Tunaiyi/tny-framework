package com.tny.game.web.converter;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.runtime.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Map;

public class ProtoExHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProtoExHttpMessageConverter.class);

	public final static MediaType PROTOBUF_EX_MEDIA_TYPE = MediaType.valueOf("application/x-protobuf-ex");

	public Map<Class<?>, Boolean> noProtoMap = new CopyOnWriteMap<>();

	public ProtoExHttpMessageConverter() {
		super(PROTOBUF_EX_MEDIA_TYPE);
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		if (mediaType != null && !mediaType.includes(PROTOBUF_EX_MEDIA_TYPE)) {
			return false;
		}
		try {
			return this.doCheck(clazz);
		} catch (Throwable e) {
			LOGGER.error("", e);
		}
		return false;
	}

	private boolean doCheck(Class<?> clazz) {
		if (this.noProtoMap.containsKey(clazz)) {
			return false;
		}
		ProtoExSchema<?> schema = RuntimeProtoExSchema.getProtoSchema(clazz);
		if (schema != null) {
			return true;
		} else {
			this.noProtoMap.put(clazz, false);
			return false;
		}
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		try {
			return this.doCheck(clazz);
		} catch (Throwable e) {
			e.printStackTrace();
			LOGGER.error("error", e);
		}
		return false;
	}

	@Override
	protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		byte[] buf = StreamUtils.copyToByteArray(inputMessage.getBody());
		try (ProtoExReader reader = new ProtoExReader(buf)) {
			return reader.readMessage(clazz);
		}
	}

	@Override
	protected void writeInternal(Object value, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		byte[] data;
		if (value instanceof byte[]) {
			data = (byte[])value;
		} else {
			try (ProtoExWriter writer = new ProtoExWriter(128)) {
				writer.writeMessage(value, TypeEncode.EXPLICIT);
				data = writer.toByteArray();
			}
		}
		if (data != null) {
			outputMessage.getHeaders().setContentLength(data.length);
			StreamUtils.copy(data, outputMessage.getBody());
		}
	}

}
