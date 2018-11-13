package com.tny.game.net.netty4;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.UnitLoader;
import com.tny.game.net.base.*;
import com.tny.game.net.command.MessageHandler;
import com.tny.game.net.message.MessageFactory;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.*;

import static com.tny.game.common.lifecycle.LifecycleLevel.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public abstract class NettyBootstrap implements AppPrepareStart {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    private NettyUnitSetting unitSetting;

    protected ChannelMaker<Channel> channelMaker;

    protected MessageHandler<Object> messageHandler;

    protected MessageFactory<Object> messageFactory;

    public NettyBootstrap(NettyUnitSetting unitSetting) {
        this.unitSetting = unitSetting;
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

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    protected <T> MessageFactory<T> getMessageFactory() {
        return as(messageFactory);
    }

    @Override
    public void prepareStart() {
        this.channelMaker = UnitLoader.getLoader(ChannelMaker.class).getUnitAnCheck(unitSetting.getChannelMaker());
        this.messageHandler = as(UnitLoader.getLoader(MessageHandler.class).getUnitAnCheck(unitSetting.getMessageHandler()));
        this.messageFactory = as(UnitLoader.getLoader(MessageFactory.class).getUnitAnCheck(unitSetting.getMessageFactory()));
    }

}