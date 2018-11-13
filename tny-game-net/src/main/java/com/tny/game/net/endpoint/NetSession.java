package com.tny.game.net.endpoint;

import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID>, NetEndpoint<UID> {

    /**
     * 使用指定认证登陆
     *
     * @param tunnel 指定认证
     */
    void acceptTunnel(NetTunnel<UID> tunnel) throws ValidatorFailException;

    /**
     * session下线, 不立即失效
     */
    void offline();


}
