package com.tny.game.codec.typeprotobuf;

import com.baidu.bjf.remoting.protobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.common.utils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 12:49 下午
 */
public class TypeProtobufScheme<T> {

	private final int id;

	private final Class<T> type;

	private final Codec<T> codec;

	TypeProtobufScheme(Class<T> type) {
		this.type = type;
		TypeProtobuf typeProtobuf = this.type.getAnnotation(TypeProtobuf.class);
		this.id = typeProtobuf.value();
		Asserts.checkNotNull(typeProtobuf, "{} class annotation {} no exist",
				type, TypeProtobuf.class);
		this.codec = ProtobufProxy.create(type);
	}

	public int getId() {
		return this.id;
	}

	public Class<T> getType() {
		return this.type;
	}

	public Codec<T> getCodec() {
		return this.codec;
	}

}
