package com.tny.game.net.netty4.cloud.event;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.configuration.application.*;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/8 2:58 下午
 */
public class ServerGuidePreRegisteredEvent extends ApplicationEvent {

    private final NetApplication application;

    private final ServerGuide serverGuide;

    public ServerGuidePreRegisteredEvent(NetApplication application, ServerGuide serverGuide) {
        super(application);
        this.application = application;
        this.serverGuide = serverGuide;
    }

    public NetApplication getApplication() {
        return application;
    }

    public ServerGuide getServerGuide() {
        return serverGuide;
    }

}
