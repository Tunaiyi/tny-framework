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

	DoneResult<RpcLinkerId> auth(String service, long serverId, long instance, String password);

	String createToken(String serviceName, RpcLinkerId id);

	DoneResult<RpcToken> verify(String token);

}
