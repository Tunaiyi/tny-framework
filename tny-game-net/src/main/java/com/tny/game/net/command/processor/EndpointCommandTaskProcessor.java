package com.tny.game.net.command.processor;

import com.tny.game.net.endpoint.task.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 11:27 上午
 */
public abstract class EndpointCommandTaskProcessor<T extends EndpointCommandTaskTrigger> implements CommandTaskProcessor {

    private int busSpinTimes = 10;

    private int yieldTimes = 10;

    @Override
    public void submit(CommandTaskBox box) {
        EndpointCommandTaskTrigger processor = box.getAttachment(this);
        if (processor == null) {
            processor = box.setAttachmentIfNull(this, () -> createTrigger(box));
        }
        processor.trySubmit();
    }

    protected abstract T createTrigger(CommandTaskBox box);

    protected abstract void process(T processor);

    protected abstract void schedule(T processor);

    public EndpointCommandTaskProcessor<T> setBusSpinTimes(int busSpinTimes) {
        this.busSpinTimes = busSpinTimes;
        return this;
    }

    public EndpointCommandTaskProcessor<T> setYieldTimes(int yieldTimes) {
        this.yieldTimes = yieldTimes;
        return this;
    }

    public int getBusSpinTimes() {
        return this.busSpinTimes;
    }

    public int getYieldTimes() {
        return this.yieldTimes;
    }

}
