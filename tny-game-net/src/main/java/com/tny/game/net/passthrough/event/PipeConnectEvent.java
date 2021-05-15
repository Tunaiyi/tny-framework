package com.tny.game.net.passthrough.event;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/28 10:41 下午
 */
public class PipeConnectEvent implements PipeEvent {

    @Override
    public PipeEventType getType() {
        return PipeEventType.PIPE;
    }

}
