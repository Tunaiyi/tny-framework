package com.tny.game.net.relay.link;

import com.tny.game.net.transport.*;

import java.util.function.Consumer;

/**
 * 转发数据发送器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:13 下午
 */
public interface RelayTransporter extends WritableConnection {

	void addOnClose(Consumer<NetRelayTransporter> onClose);

}
