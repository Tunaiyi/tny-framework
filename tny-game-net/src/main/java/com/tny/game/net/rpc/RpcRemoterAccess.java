package com.tny.game.net.rpc;

import com.tny.game.net.transport.*;

/**
 * Rpc远程接入点(链接)
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:20
 **/
public interface RpcRemoterAccess extends Sender {

    /**
     * @return 访问点 id
     */
    long getAccessId();

    /**
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isActive();

}
