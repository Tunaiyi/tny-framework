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
package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.application.*;

/**
 * 沟通器
 * 具有用户标识, 有通讯状态的对象.
 * Created by Kun Yang on 2017/3/26.
 */
public interface Communicator extends ConnectIdentity, AddressPeer {

    @Override
    default ContactType getContactType() {
        return this.getCertificate().getContactType();
    }

    @Override
    default long getContactId() {
        return this.getCertificate().getContactId();
    }

    @Override
    default long getIdentify() {
        return this.getCertificate().getIdentify();
    }

    @Override
    default Object getIdentifyToken() {
        return this.getCertificate().getIdentifyToken();
    }

    /**
     * @return 是否登陆认证
     */
    default boolean isAuthenticated() {
        return this.getCertificate().isAuthenticated();
    }

    /**
     * @return 登陆凭证
     */
    Certificate getCertificate();

    /**
     * @return 获取会话属性
     */
    Attributes attributes();

}
