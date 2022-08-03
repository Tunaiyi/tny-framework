package com.tny.game.net.base;

/**
 * Rpc 服务
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 15:11
 **/
public interface RpcServicer {

    RpcServiceType getServiceType();

    int getServerId();

}
