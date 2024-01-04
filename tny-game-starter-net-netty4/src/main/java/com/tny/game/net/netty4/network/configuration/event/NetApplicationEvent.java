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

package com.tny.game.net.netty4.network.configuration.event;

import com.tny.game.net.netty4.configuration.application.*;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/8 2:58 下午
 */
public class NetApplicationEvent extends ApplicationEvent {

    private final NetApplication application;

    public NetApplicationEvent(NetApplication application) {
        super(application);
        this.application = application;
    }

    public NetApplication getApplication() {
        return application;
    }

}
