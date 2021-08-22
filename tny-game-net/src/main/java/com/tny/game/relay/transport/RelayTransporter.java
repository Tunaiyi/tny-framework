package com.tny.game.relay.transport;

import com.tny.game.net.transport.*;
import com.tny.game.relay.*;
import com.tny.game.relay.packet.*;

/**
 * 转发数据发送器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:13 下午
 */
public interface RelayTransporter extends WritableConnection {

	/**
	 * 绑定转发线路
	 *
	 * @param link 转发线路
	 */
	void bind(NetRelayLink link);

	/**
	 * 写出数据
	 *
	 * @param packet  数据包
	 * @param promise 写出Promise
	 * @return 返回 WriteMessageFuture
	 */
	WriteMessageFuture write(RelayPacket<?> packet, WriteMessagePromise promise);

	/**
	 * 写出数据
	 *
	 * @param maker 数据包构建器
	 * @return 返回 WriteMessageFuture
	 */
	WriteMessageFuture write(RelayPacketMaker maker, WriteMessagePromise promise);

}
