package com.tny.game.net.transport;

import com.tny.game.net.base.*;
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
		return this.getNetBootstrapContext().getMessageFactory();
	}

	/**
	 * @return 凭证 factory
	 */
	default CertificateFactory<UID> getCertificateFactory() {
		return this.getNetBootstrapContext().getCertificateFactory();
	}

	/**
	 * @return 获取启动器上下文
	 */
	NetBootstrapContext<UID> getNetBootstrapContext();

	/**
	 * @return 获取绑定中断
	 */
	@Override
	NetEndpoint<UID> getEndpoint();

}

