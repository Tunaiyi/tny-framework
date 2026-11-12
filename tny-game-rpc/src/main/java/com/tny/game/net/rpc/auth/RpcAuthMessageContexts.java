/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.rpc.auth;

import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
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

    public static RequestContent authRequest(long id, String password) {
        return MessageContents.request(
                Protocols.protocol(RpcProtocol.RPC_AUTH_$_AUTHENTICATE), id, password);
    }

    public static long getIdParam(MessageParamList paramList) {
        return paramList.getLong(RPC_AUTH_ID_INDEX);
    }

    public static String getPasswordParam(MessageParamList paramList) {
        return paramList.getString(RPC_AUTH_PASSWORD_INDEX);
    }

}
