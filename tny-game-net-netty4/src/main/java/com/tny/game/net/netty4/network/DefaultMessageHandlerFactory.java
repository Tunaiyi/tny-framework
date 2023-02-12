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
package com.tny.game.net.netty4.network;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/11/14 03:54
 **/
@Unit
public class DefaultMessageHandlerFactory implements NettyMessageHandlerFactory {

    @Override
    public NettyMessageHandler create(NetworkContext context) {
        return new NettyMessageHandler(context);
    }

}