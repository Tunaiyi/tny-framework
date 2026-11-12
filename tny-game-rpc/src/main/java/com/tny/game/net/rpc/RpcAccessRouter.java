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
package com.tny.game.net.rpc;

import com.tny.game.net.message.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:15 上午
 */
public class RpcAccessRouter implements RpcRouter {

    @Override
    public RpcAccess route(RpcInvokeNodeSet serviceSet, RpcRemoteMethod invoker, RpcRemoteInvokeParams invokeParams) {
        Contact contact = invokeParams.getReceiver();
        if (contact == null) {
            throw new NullPointerException(format("invoke {} Receiver is null", invoker));
        }
        return serviceSet.findInvokeAccess(0, contact.getContactId());
    }

}
