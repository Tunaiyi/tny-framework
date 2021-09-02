package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
@ProtobufClass
public class LinkOpenArgumentsProto extends BaseLinkArgumentsProto<LinkOpenArguments> {

	@Protobuf(order = 1)
	private String cluster;

	@Protobuf(order = 2, fieldType = FieldType.FIXED64)
	private long instance;

	@Protobuf(order = 3)
	private String key;

	public LinkOpenArgumentsProto() {
	}

	public LinkOpenArgumentsProto(LinkOpenArguments arguments) {
		super(arguments);
		this.cluster = arguments.getCluster();
		this.instance = arguments.getInstance();
		this.key = arguments.getKey();
	}

	@Override
	public LinkOpenArguments toArguments() {
		return new LinkOpenArguments(this.cluster, this.instance, this.key);
	}

	public String getCluster() {
		return cluster;
	}

	public long getInstance() {
		return instance;
	}

	public String getKey() {
		return key;
	}

}
