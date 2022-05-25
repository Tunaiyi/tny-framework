package com.tny.game.net.rpc;

import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 3:26 下午
 */
public interface RpcRemoterManager {

    RpcRemoterSet loadRemoterSet(MessagerType type);

    RpcRemoterSet findRemoterSet(MessagerType type);

}
