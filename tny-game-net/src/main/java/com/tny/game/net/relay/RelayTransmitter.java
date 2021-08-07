package com.tny.game.net.relay;

import com.tny.game.net.transport.*;

/**
 * 透传数据发送器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:13 下午
 */
public interface RelayTransmitter extends Connection, RelayTransmissible {

	void bind(GeneralPipe<?> pipe);

}
