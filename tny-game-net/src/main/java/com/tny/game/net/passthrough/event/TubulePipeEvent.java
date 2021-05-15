package com.tny.game.net.passthrough.event;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public interface TubulePipeEvent<UID> extends PipeEvent {

    long getTunnelId();

    UID getUserId();

    String getUserType();

    @Override
    default PipeEventType getType() {
        return PipeEventType.TUBULE;
    }

}
