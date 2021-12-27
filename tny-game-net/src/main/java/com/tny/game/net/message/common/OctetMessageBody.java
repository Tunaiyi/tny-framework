package com.tny.game.net.message.common;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 16:37
 */
public interface OctetMessageBody {

	static void release(OctetMessageBody body) {
		if (body != null) {
			body.release();
		}
	}

	boolean isRelay();

	Object getBody();

	void release();

}
