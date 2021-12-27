package com.tny.game.net.message.common;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 16:37
 */
public class ByteArrayMessageBody implements OctetMessageBody {

	/**
	 * 消息体字节
	 */
	private byte[] bodyBytes;

	/**
	 * 是否是转发
	 */
	private final boolean relay;

	public ByteArrayMessageBody(byte[] bodyBytes, boolean relay) {
		this.bodyBytes = bodyBytes;
		this.relay = relay;
	}

	@Override
	public boolean isRelay() {
		return relay;
	}

	@Override
	public byte[] getBody() {
		return bodyBytes;
	}

	@Override
	public void release() {
		bodyBytes = null;
	}

}
