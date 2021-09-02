package com.tny.game.net.netty4.datagram;

import com.tny.game.net.transport.*;
import io.netty.channel.ChannelPromise;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/27 5:31 下午
 */
public class MessageChannelPromises {

	private ChannelPromise channelPromise;

	private WriteMessagePromise writePromise;

	public ChannelPromise getChannelPromise() {
		return channelPromise;
	}

	public WriteMessagePromise getWritePromise() {
		return writePromise;
	}

}
