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

package com.tny.game.net.netty4.relay;

import com.tny.game.net.application.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public abstract class NettyServerBootstrap<S extends ServerBootstrapSetting> extends NettyBootstrap<S> {


    public NettyServerBootstrap(NetAppContext appContext, S unitSetting) {
        super(appContext, unitSetting);
    }

    public NettyServerBootstrap(NetAppContext appContext, S unitSetting, ChannelMaker<Channel> channelMaker) {
        super(appContext, unitSetting, channelMaker);
    }

    protected void init(ServerBootstrap bootstrap, EventLoopGroup parentGroup, EventLoopGroup childGroup, boolean epoll) {
        bootstrap.group(parentGroup, childGroup);
        bootstrap.channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    }


}
