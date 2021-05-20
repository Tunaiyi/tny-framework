package com.tny.game.net.netty4.processor;

import com.tny.game.net.command.processor.*;
import com.tny.game.net.endpoint.task.*;
import io.netty.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 10:47 下午
 */
public class TimerEndpointCommandTaskTrigger extends EndpointCommandTaskTrigger implements TimerTask {

    protected TimerEndpointCommandTaskTrigger(CommandTaskBox taskBox,
            EndpointCommandTaskProcessor<?> processor) {
        super(taskBox, processor);
    }

    @Override
    public void run(Timeout timeout) {
        if (!timeout.isCancelled()) {
            this.trySubmit();
        }
    }

}
