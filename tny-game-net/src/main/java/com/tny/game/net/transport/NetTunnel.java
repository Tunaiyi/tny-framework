package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel<UID> extends Tunnel<UID>, Transport, Receiver, Sender {

	/**
	 * 设置访问 Id
	 *
	 * @param accessId 访问 Id
	 */
	void setAccessId(long accessId);

	/**
	 * ping
	 */
	void ping();

	/**
	 * pong
	 */
	void pong();

	/**
	 * 打开
	 */
	boolean open();

	/**
	 * 断开
	 */
	void disconnect();

	/**
	 * 断开并重置状态
	 */
	void reset();

	/**
	 * 终端 Endpoint
	 *
	 * @param endpoint 终端
	 * @return 返回是否绑定成功
	 */
	boolean bind(NetEndpoint<UID> endpoint);

	/**
	 * @return message factory
	 */
	default MessageFactory getMessageFactory() {
		return this.getContext().getMessageFactory();
	}

	/**
	 * @return 凭证 factory
	 */
	default CertificateFactory<UID> getCertificateFactory() {
		return this.getContext().getCertificateFactory();
	}

	/**
	 * @return 获取上下文
	 */
	NetworkContext getContext();

	/**
	 * @return 获取绑定中断
	 */
	@Override
	NetEndpoint<UID> getEndpoint();

}

