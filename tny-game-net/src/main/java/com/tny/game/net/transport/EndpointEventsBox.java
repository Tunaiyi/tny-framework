package com.tny.game.net.transport;

import com.tny.game.net.endpoint.event.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Session 输入输出事件箱
 * Created by Kun Yang on 2017/3/23.
 */
public class EndpointEventsBox<UID> {

    /* 接收队列 */
    private final Queue<EndpointInputEvent<UID>> inputEventQueue = new ConcurrentLinkedQueue<>();

    /* 发送队列 */
    private final Queue<EndpointOutputEvent<UID>> outputEventQueue = new ConcurrentLinkedQueue<>();

    public boolean hasInputEvent() {
        return !this.inputEventQueue.isEmpty();
    }

    public boolean hasOutputEvent() {
        return !this.outputEventQueue.isEmpty();
    }

    public void accept(EndpointEventsBox<UID> eventBox) {
        this.outputEventQueue.addAll(eventBox.outputEventQueue);
        this.inputEventQueue.addAll(eventBox.inputEventQueue);
    }

    public void addOutputEvent(EndpointOutputEvent<UID> event) {
        this.outputEventQueue.add(event);
    }

    public void addInputEvent(EndpointInputEvent<UID> event) {
        this.inputEventQueue.add(event);
    }

    public EndpointInputEvent<UID> pollInputEvent() {
        if (this.inputEventQueue.isEmpty()) {
            return null;
        }
        return this.inputEventQueue.poll();
    }

    public EndpointOutputEvent<UID> pollOutputEvent() {
        if (this.outputEventQueue.isEmpty()) {
            return null;
        }
        return this.outputEventQueue.poll();
    }

}
