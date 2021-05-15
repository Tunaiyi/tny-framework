package com.tny.game.net.passthrough.event;

import com.tny.game.net.message.*;
import com.tny.game.net.passthrough.*;
import com.tny.game.net.transport.*;

/**
 * 传输事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TubuleMessagePipeEvent<UID> extends BaseTubulePipeEvent<UID> {

    private final Message message;

    private final WriteMessagePromise promise;

    public TubuleMessagePipeEvent(Tubule<UID> tubule, Message message, WriteMessagePromise promise) {
        super(tubule);
        this.message = message;
        this.promise = promise;
    }

    @Override
    public PipeEventType getType() {
        return PipeEventType.MESSAGE;
    }

    public Message getMessage() {
        return this.message;
    }

    public WriteMessagePromise getPromise() {
        return this.promise;
    }

}