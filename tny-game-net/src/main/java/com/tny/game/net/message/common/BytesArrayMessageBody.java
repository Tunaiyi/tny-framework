package com.tny.game.net.message.common;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 16:37
 */
public class BytesArrayMessageBody implements MessageBytesBody {

	private final byte[] bodyBytes;

	public BytesArrayMessageBody(byte[] bodyBytes) {
		this.bodyBytes = bodyBytes;
	}

	@Override
	public byte[] getBodyBytes() {
		return bodyBytes;
	}

}
