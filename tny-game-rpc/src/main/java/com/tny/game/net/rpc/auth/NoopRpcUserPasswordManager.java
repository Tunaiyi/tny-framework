package com.tny.game.net.rpc.auth;

import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:55 上午
 */
public class NoopRpcUserPasswordManager implements RpcUserPasswordManager {

    @Override
    public boolean auth(RpcAccessIdentify identify, String password) {
        return true;
    }

}
