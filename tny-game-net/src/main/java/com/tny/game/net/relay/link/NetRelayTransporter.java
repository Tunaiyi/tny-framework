package com.tny.game.net.relay.link;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.transport.*;

/**
 * 转发数据发送器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:13 下午
 */
public interface NetRelayTransporter extends RelayTransporter {

	/**
	 * 绑定转发线路
	 *
	 * @param link 转发线路
	 */
	void bind(NetRelayLink link);

	/**
	 * @return 获取启动器上下文
	 */
	NetworkContext getContext();

	/**
	 * 写出数据
	 *
	 * @param packet  数据包
	 * @param awaiter 写出Promise
	 * @return 返回 MessageWriteAwaiter
	 */
	MessageWriteAwaiter write(RelayPacket<?> packet, MessageWriteAwaiter awaiter);

	/**
	 * 写出数据
	 *
	 * @param maker 数据包构建器
	 * @return 返回 MessageWriteAwaiter
	 */
	MessageWriteAwaiter write(RelayPacketMaker maker, MessageWriteAwaiter awaiter);

}
