package com.tny.game.net.rpc.auth;

import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;

import javax.annotation.Resource;

import static com.tny.game.net.rpc.auth.RpcProtocol.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@RpcController
public class RpcAuthController {

    @Resource
    private RpcAuthService rpcAuthService;

    @RpcRequest(RPC_AUTH_$_AUTHENTICATE)
    @AuthenticationRequired(validator = RpcPasswordValidator.class)
    public RpcResult<String> authenticate(ServerBootstrapSetting setting, @UserId RpcAccessIdentify id) {
        RpcServiceType serviceType = RpcServiceTypes.checkService(setting.serviceName());
        String token = rpcAuthService.createToken(serviceType, id);
        return RpcResults.success(token);
    }

    @RpcResponse(RPC_AUTH_$_AUTHENTICATE)
    @AuthenticationRequired(validator = RpcTokenValidator.class)
    public void authenticated(@UserId RpcAccessIdentify id) {
    }

}
