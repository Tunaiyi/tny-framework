package com.tny.game.net.netty4.cloud.event;

import com.tny.game.net.netty4.configuration.application.*;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/9 4:59 下午
 */
public class NetApplicationPreRegisteredEvent extends ApplicationEvent {

    private final NetApplication application;

    public NetApplicationPreRegisteredEvent(NetApplication application) {
        super(application);
        this.application = application;
    }

    public NetApplication getApplication() {
        return application;
    }

}
