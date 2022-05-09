package com.tny.game.net.rpc.auth;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 5:39 下午
 */
public interface RpcAuthService {

    DoneResult<RpcAccessIdentify> authenticate(long id, String password);

    String createToken(RpcServiceType serviceType, RpcAccessIdentify removeIdentify);

    DoneResult<RpcAccessToken> verifyToken(String token);

}
