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
