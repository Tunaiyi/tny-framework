package com.tny.game.net.relay.link;

import com.tny.game.net.relay.cluster.*;

import java.util.List;

/**
 * 本地(客户端连接在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
public interface NetRemoteRelayExplorer extends RemoteRelayExplorer {

    void putInstance(ServeNode node);

    void removeInstance(ServeNode node);

    void updateInstance(ServeNode node, List<ServeNodeChangeStatus> statuses);

}
