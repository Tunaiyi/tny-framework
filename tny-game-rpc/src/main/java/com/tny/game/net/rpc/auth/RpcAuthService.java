package com.tny.game.net.rpc.auth;

import com.tny.game.common.result.*;
import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 5:39 下午
 */
public interface RpcAuthService {

    DoneResult<RpcAccessId> authenticate(String service, long serverId, long instance, String password);

    String createToken(String serviceName, RpcAccessId id);

    DoneResult<RpcToken> verifyToken(String token);

}
