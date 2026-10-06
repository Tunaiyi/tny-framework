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

package com.tny.game.net.netty4;

import com.tny.game.net.application.*;
import com.tny.game.net.netty4.channel.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public abstract class NettyBootstrap<S extends NetBootstrapSetting> extends NetBootstrap<S> {

    protected ChannelMaker<Channel> channelMaker;

    public NettyBootstrap(NetAppContext appContext, S unitSetting) {
        super(appContext, unitSetting);
    }

    public NettyBootstrap(NetAppContext appContext, S unitSetting, ChannelMaker<Channel> channelMaker) {
        super(appContext, unitSetting);
        this.channelMaker = channelMaker;
    }


    protected void init(ServerBootstrap bootstrap, EventLoopGroup parentGroup, EventLoopGroup childGroup, boolean epoll) {
        bootstrap.group(parentGroup, childGroup);
        bootstrap.channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    }


    protected static boolean isEpoll() {
        String osName = System.getProperties().getProperty("os.name");
        String osVersion = System.getProperties().getProperty("os.version");
        if ("Linux".equals(osName)) {
            String[] versions = osVersion.split("\\.", 0);
            if (versions.length >= 2) {
                try {
                    int major = Integer.parseInt(versions[0]);
                    int minor = Integer.parseInt(versions[1]);
                    if (major > 2 || (major == 2 && minor >= 6)) {
                        return true;
                    }
                } catch (NumberFormatException x) {
                    // format not recognized
                }
            }
        }
        return false;
    }

    public static EventLoopGroup createLoopGroup(boolean epoll, int threads, String name) {
        if (epoll) {
            return new EpollEventLoopGroup(threads, new DefaultThreadFactory(name, true, 8));
        } else {
            return new NioEventLoopGroup(threads, new DefaultThreadFactory(name, true, 8));
        }
        //        if (epoll) {
        //            return new EpollEventLoopGroup(threads,
        //                    new ThreadPerTaskExecutor(new DefaultThreadFactory(name, true, 8)),
        //                    DefaultEventExecutorChooserFactory.INSTANCE,
        //                    DefaultSelectStrategyFactory.INSTANCE,
        //                    RejectedExecutionHandlers.reject(),
        //                    (size) -> new ConcurrentLinkedQueue<>());
        //        } else {
        //            return new NioEventLoopGroup(threads,
        //                    new ThreadPerTaskExecutor(new DefaultThreadFactory(name, true, 8)),
        //                    DefaultEventExecutorChooserFactory.INSTANCE,
        //                    SelectorProvider.provider(),
        //                    DefaultSelectStrategyFactory.INSTANCE,
        //                    RejectedExecutionHandlers.reject(),
        //                    (size) -> new ConcurrentLinkedQueue<>());
        //        }
    }

}
