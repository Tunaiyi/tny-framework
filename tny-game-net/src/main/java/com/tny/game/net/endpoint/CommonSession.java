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
package com.tny.game.net.endpoint;

import com.google.common.base.MoreObjects;
import com.tny.game.net.transport.*;

/**
 * 抽象Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class CommonSession extends BaseNetEndpoint implements NetSession {

    public CommonSession(SessionSetting setting, Certificate certificate, EndpointContext endpointContext) {
        super(certificate, endpointContext, setting.getSendMessageCachedSize());
    }

    @Override
    public void onUnactivated(NetTunnel tunnel) {
        if (isOffline()) {
            return;
        }
        synchronized (this) {
            if (isOffline()) {
                return;
            }
            Tunnel currentTunnel = this.tunnel();
            if (currentTunnel.isActive()) {
                return;
            }
            if (isClosed()) {
                return;
            }
            setOffline();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("contactGroup", this.getGroup())
                .add("identify", this.getIdentify())
                .add("tunnel", this.tunnel())
                .toString();
    }

}
