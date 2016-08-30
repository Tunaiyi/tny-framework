package com.tny.game.web.converter;

import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.protoex.ProtoExReader;
import com.tny.game.protoex.ProtoExSchema;
import com.tny.game.protoex.ProtoExWriter;
import com.tny.game.protoex.annotations.TypeEncode;
import com.tny.game.protoex.field.runtime.RuntimeProtoExSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Map;

public class ProtoExHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProtoExHttpMessageConverter.class);

	public final static MediaType PROTOBUF_EX_MEDIA_TYPE = MediaType.valueOf("application/x-protobuf-ex");

	public Map<Class<?>, Boolean> noProtoMap = new CopyOnWriteMap<Class<?>, Boolean>();

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
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		byte[] buf = StreamUtils.copyToByteArray(inputMessage.getBody());
		ProtoExReader reader = new ProtoExReader(buf);
		return reader.readMessage(clazz);
	}

	@Override
	protected void writeInternal(Object value, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		byte[] data = null;
		if (value instanceof byte[]) {
			data = (byte[]) value;
		} else {
			ProtoExWriter writer = new ProtoExWriter(128);
			writer.writeMessage(value, TypeEncode.EXPLICIT);
			data = writer.toByteArray();
		}
		if (data != null) {
			outputMessage.getHeaders().setContentLength(data.length);
			StreamUtils.copy(data, outputMessage.getBody());
		}
	}

}