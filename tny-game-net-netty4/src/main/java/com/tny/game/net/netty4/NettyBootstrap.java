package com.tny.game.net.netty4;

import com.tny.game.common.unit.*;
import com.tny.game.net.base.*;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public abstract class NettyBootstrap<S extends NettyBootstrapSetting> extends NetBootstrap<S> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    protected ChannelMaker<Channel> channelMaker;

    public NettyBootstrap(S unitSetting) {
        super(unitSetting);
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
    }

    @Override
    public void postPrepared(S setting) {
        this.channelMaker = UnitLoader.getLoader(ChannelMaker.class).getUnitAnCheck(setting.getChannelMaker());
    }

}
