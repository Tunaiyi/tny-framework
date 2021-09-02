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
public class TunnelConnectArgumentsProto extends BaseTunnelArgumentsProto<TunnelConnectArguments> {

	@Packed
	@Protobuf(order = 10)
	private byte[] ipValue = new byte[4];

	@Protobuf(order = 11)
	private int port;

	public TunnelConnectArgumentsProto() {
	}

	public TunnelConnectArgumentsProto(TunnelConnectArguments arguments) {
		super(arguments);
		int[] ipValue = arguments.getIpValue();
		for (int i = 0; i < ipValue.length; i++) {
			this.ipValue[i] = (byte)ipValue[i];
		}
		this.port = arguments.getPort();
	}

	public byte[] getIpValue() {
		return ipValue;
	}

	public TunnelConnectArgumentsProto setIpValue(byte[] ipValue) {
		this.ipValue = ipValue;
		return this;
	}

	public int getPort() {
		return port;
	}

	public TunnelConnectArgumentsProto setPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	public TunnelConnectArguments toArguments() {
		return new TunnelConnectArguments(this.getTunnelId(), this.ipValue, this.port);
	}

}
