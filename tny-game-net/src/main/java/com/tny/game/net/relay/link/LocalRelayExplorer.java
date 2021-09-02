package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.net.transport.*;

/**
 * 本地(客户端连接在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
@UnitInterface
public interface LocalRelayExplorer {

	<T> DoneResult<LocalRelayTunnel<T>> bindTunnel(NetTunnel<T> tunnel);

}
