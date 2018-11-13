package com.tny.game.net.endpoint;


import com.tny.game.net.exception.ValidatorFailException;

/**
 * 客户端持有器
 *
 * @param <UID>
 */
public interface ClientKeeper<UID> extends EndpointKeeper<UID, Client<UID>> {

    /**
     * 注册客户端
     */
    void register(Client<UID> client);

    /**
     * 注销终端
     */
    void unregister(Client<UID> client) throws ValidatorFailException;

}
