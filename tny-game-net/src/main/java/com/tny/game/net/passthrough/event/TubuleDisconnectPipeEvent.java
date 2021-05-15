package com.tny.game.net.passthrough.event;

import com.tny.game.net.passthrough.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TubuleDisconnectPipeEvent<UID> extends BaseTubulePipeEvent<UID> {

    public TubuleDisconnectPipeEvent(Tubule<UID> tubule) {
        super(tubule);
    }

    @Override
    public PipeEventType getType() {
        return PipeEventType.TUBULE;
    }

}
