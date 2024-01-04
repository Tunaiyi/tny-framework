/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.application.*;

/**
 * 联系人
 * 具有用户标识, 有通讯状态的对象.
 * Created by Kun Yang on 2017/3/26.
 */
public interface Connector extends ConnectIdentity, AddressPeer {

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
     * @return 登陆凭证
     */
    Certificate getCertificate();

    /**
     * @return 是否登陆认证
     */
    default boolean isAuthenticated() {
        return this.getCertificate().isAuthenticated();
    }

    /**
     * @return 获取会话属性
     */
    Attributes attributes();

}
