package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.cloud.nacos.event.NacosDiscoveryInfoChangedEvent;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.netty4.network.configuration.event.*;
import org.springframework.context.event.EventListener;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/9 6:35 下午
 */
public class NacosEventListener implements AppClosed {

    private final NetAutoServiceRegister netAutoServiceRegister;

    public NacosEventListener(NetAutoServiceRegister netAutoServiceRegister) {
        this.netAutoServiceRegister = netAutoServiceRegister;
    }

    @EventListener
    public void onNacosDiscoveryInfoChangedEvent(NacosDiscoveryInfoChangedEvent event) {
        this.netAutoServiceRegister.restart();
    }

    @EventListener
    public void onNetApplicationStartEvent(NetApplicationStartEvent event) {
        this.netAutoServiceRegister.start();
    }

    @Override
    public void onClosed() {
        this.netAutoServiceRegister.stop();
    }

}
