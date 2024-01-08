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
