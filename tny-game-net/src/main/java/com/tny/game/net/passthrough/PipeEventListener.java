package com.tny.game.net.passthrough;

import com.tny.game.net.passthrough.event.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/28 10:39 下午
 */
public interface PipeEventListener {

    void onConnect(PipeDisconnectEvent event);

    void onDisconnect(PipeDisconnectEvent event);

}
