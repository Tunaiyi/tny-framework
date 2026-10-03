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
package com.tny.game.net.session;

import com.google.common.base.MoreObjects;
import com.tny.game.net.transport.*;

/**
 * 抽象Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class CommonSession extends BaseNetSession implements NetSession {

    public CommonSession(SessionContext sessionContext, NetTunnel tunnel) {
        super(Certificates.anonymous(), sessionContext, tunnel, 0);
    }

    public CommonSession(Certificate certificate, SessionContext sessionContext, NetTunnel tunnel, int cacheSize) {
        super(certificate, sessionContext, tunnel, cacheSize);
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
