package com.tny.game.net.netty4.processor.disruptor;

import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.task.*;
import io.netty.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 10:47 下午
 */
public class TimerCommandTaskBoxDriver extends CommandTaskBoxDriver implements TimerTask {

    protected TimerCommandTaskBoxDriver(CommandTaskBox taskBox, EndpointCommandTaskBoxProcessor<?> processor) {
        super(taskBox, processor);
    }

    @Override
    public void run(Timeout timeout) {
        if (!timeout.isCancelled()) {
            this.trySubmit();
        }
    }

}
