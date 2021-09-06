package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

import java.util.*;

/**
 * 本地(客户端连接在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
@UnitInterface
public interface LocalRelayExplorer extends RelayExplorer<LocalRelayTunnel<?>> {

	LocalServeCluster getCluster(String id);

	List<LocalServeCluster> getClusters();

	Map<String, LocalServeCluster> getClusterMap();

	<D> DoneResult<LocalRelayTunnel<D>> createTunnel(long id, MessageTransporter<D> transport, NetworkContext context);

	<D> LocalRelayLink allotLink(LocalRelayTunnel<D> tunnel, String cluster);

}
