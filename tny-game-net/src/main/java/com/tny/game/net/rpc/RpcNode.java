package com.tny.game.net.rpc;

import com.tny.game.net.base.*;

/**
 * 节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/6/2 04:36
 **/
public interface RpcNode {

    /**
     * @return 服务器 id
     */
    int getNodeId();

    /**
     * @return 服务类型
     */
    RpcServiceType getServiceType();

}
