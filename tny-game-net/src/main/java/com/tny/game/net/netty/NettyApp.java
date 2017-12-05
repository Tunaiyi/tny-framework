package com.tny.game.net.netty;

import com.tny.game.suite.app.NetLogger;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public abstract class NettyApp {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    protected String name;

    public NettyApp(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
        if (epoll)
            return new EpollEventLoopGroup(threads, new DefaultThreadFactory(name, true));
        else
            return new NioEventLoopGroup(threads, new DefaultThreadFactory(name, true));
    }


}
