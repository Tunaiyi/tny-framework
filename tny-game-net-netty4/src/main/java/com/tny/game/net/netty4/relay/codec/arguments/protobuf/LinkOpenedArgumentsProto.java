package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
@ProtobufClass
public class LinkOpenedArgumentsProto extends BaseLinkArgumentsProto<LinkOpenedArguments> {

	@Protobuf(order = 1)
	private boolean result;

	public LinkOpenedArgumentsProto() {
		super();
	}

	public LinkOpenedArgumentsProto(LinkOpenedArguments arguments) {
		this.result = arguments.getResult();
	}

	@Override
	public LinkOpenedArguments toArguments() {
		return LinkOpenedArguments.of(this.result);
	}

	public boolean isResult() {
		return result;
	}

	public LinkOpenedArgumentsProto setResult(boolean result) {
		this.result = result;
		return this;
	}

}
