package com.tny.game.actor.local;

import com.tny.game.actor.event.Error;
import com.tny.game.actor.event.EventStream;
import com.tny.game.actor.exception.NonFatal;

/**
 * 任务执行调用对象
 * Created by Kun Yang on 16/1/21.
 */
public class TaskInvocation implements Runnable {

    private EventStream eventStream;
    private Runnable runnable;
    private Cleanup cleanup;

    TaskInvocation(EventStream eventStream, Runnable runnable, Cleanup cleanup) {
        this.eventStream = eventStream;
        this.runnable = runnable;
        this.cleanup = cleanup;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Throwable e) {
            if (NonFatal.isNonFatal(e)) {
                eventStream.publish(Error.error(e, "TaskInvocation", this.getClass(), e.getMessage()));
            }
        } finally {
            cleanup.cleanup();
        }
    }

}
