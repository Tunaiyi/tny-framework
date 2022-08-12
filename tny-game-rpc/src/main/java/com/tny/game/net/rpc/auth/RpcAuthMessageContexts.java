/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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

    private static final int RPC_AUTH_ID_INDEX = 0;

    private static final int RPC_AUTH_PASSWORD_INDEX = 1;

    public static RequestContext authRequest(long id, String password) {
        return MessageContexts.request(
                Protocols.protocol(RpcProtocol.RPC_AUTH_$_AUTHENTICATE), id, password);
    }

    public static long getIdParam(MessageParamList paramList) {
        return paramList.getLong(RPC_AUTH_ID_INDEX);
    }

    public static String getPasswordParam(MessageParamList paramList) {
        return paramList.getString(RPC_AUTH_PASSWORD_INDEX);
    }

}
