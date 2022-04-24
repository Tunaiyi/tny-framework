package com.tny.game.net.rpc.auth;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:55 上午
 */
public interface RpcUserPasswordManager {

    boolean auth(String service, long serverId, long instance, String password);

}
