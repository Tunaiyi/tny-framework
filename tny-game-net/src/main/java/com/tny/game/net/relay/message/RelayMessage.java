package com.tny.game.net.relay.message;

import com.tny.game.common.context.*;
import com.tny.game.common.type.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 5:48 下午
 */
public class RelayMessage implements NetMessage {

	/**
	 * 释放需要转发
	 */
	private final boolean relay;

	/**
	 * 消息
	 */
	private final NetMessage message;

	public RelayMessage(NetMessage message) {
		this.message = message;
		Object body = message.getBody();
		if (body instanceof OctetMessageBody) {
			relay = ((OctetMessageBody)body).isRelay();
		} else {
			relay = false;
		}
	}

	public boolean isRelay() {
		return relay;
	}

	@Override
	public long getId() {
		return message.getId();
	}

	@Override
	public int getLine() {
		return message.getLine();
	}

	@Override
	public MessageMode getMode() {
		return message.getMode();
	}

	@Override
	public long getToMessage() {
		return message.getToMessage();
	}

	@Override
	public int getCode() {
		return message.getCode();
	}

	@Override
	public int getProtocolId() {
		return message.getProtocolId();
	}

	@Override
	public long getTime() {
		return message.getTime();
	}

	@Override
	public MessageHead getHead() {
		return message.getHead();
	}

	@Override
	public Attributes attributes() {
		return message.attributes();
	}

	@Override
	public boolean existBody() {
		return message.existBody();
	}

	@Override
	public Object getBody() {
		return message.getBody();
	}

	@Override
	public <T> T bodyAs(Class<T> clazz) {
		return message.bodyAs(clazz);
	}

	@Override
	public <T> T bodyAs(ReferenceType<T> clazz) {
		return message.bodyAs(clazz);
	}

	@Override
	public MessageType getType() {
		return message.getType();
	}

	@Override
	public void allotMessageId(long id) {
		message.allotMessageId(id);
	}

	@Override
	public boolean isOwn(Protocol protocol) {
		return message.isOwn(protocol);
	}

	public void release() {
		OctetMessageBody body = message.bodyAs(OctetMessageBody.class);
		if (body != null) {
			body.release();
		}
	}

}
