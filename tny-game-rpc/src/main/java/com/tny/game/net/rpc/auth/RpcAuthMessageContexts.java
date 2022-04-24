package com.tny.game.net.rpc.auth;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 4:58 下午
 */
public class RpcAuthMessageContexts {

    private static final int RPC_AUTH_SERVICE_INDEX = 0;

    private static final int RPC_AUTH_SERVER_ID_INDEX = 1;

    private static final int RPC_AUTH_INSTANCE_INDEX = 2;

    private static final int RPC_AUTH_PASSWORD_INDEX = 3;

    public static RequestContext authRequest(String service, long serverId, long instance, String password) {
        return MessageContexts.request(
                Protocols.protocol(RpcProtocol.RPC_AUTH_$_AUTHENTICATE),
                service, serverId, instance, password);
    }

    public static String getServiceParam(MessageParamList paramList) {
        return paramList.getString(RPC_AUTH_SERVICE_INDEX);
    }

    public static long getServerIdParam(MessageParamList paramList) {
        return paramList.getLong(RPC_AUTH_SERVER_ID_INDEX);
    }

    public static long getInstanceIdParam(MessageParamList paramList) {
        return paramList.getLong(RPC_AUTH_INSTANCE_INDEX);
    }

    public static String getPasswordParam(MessageParamList paramList) {
        return paramList.getString(RPC_AUTH_PASSWORD_INDEX);
    }

}
