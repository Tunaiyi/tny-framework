package com.tny.game.net.rpc;

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;

/**
 * 转发接入
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 20:11
 **/
public interface RpcForwardAccess extends RpcRemoterAccess {

    /**
     * @return 服务 id
     */
    RpcAccessIdentify getIdentify();

    /**
     * @return 获取转发服务者
     */
    ForwardPoint getForwardPoint();

}
