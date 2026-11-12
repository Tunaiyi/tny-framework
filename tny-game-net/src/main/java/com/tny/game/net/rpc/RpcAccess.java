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

import com.tny.game.net.session.*;

/**
 * Rpc远程接入点(链接)
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:20
 **/
public interface RpcAccess {

    /**
     * @return 访问点 id
     */
    long getAccessId();

    /**
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isActive();

    /**
     * @return session
     */
    Session getSession();
}
