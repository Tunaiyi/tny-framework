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
    private Queue<EndPointInputEvent<UID>> inputEventQueue = new ConcurrentLinkedQueue<>();

    /* 发送队列 */
    private Queue<EndPointOutputEvent<UID>> outputEventQueue = new ConcurrentLinkedQueue<>();


    public boolean hasInputEvent() {
        return !inputEventQueue.isEmpty();
    }

    public boolean hasOutputEvent() {
        return !outputEventQueue.isEmpty();
    }

    public void accept(EndpointEventsBox<UID> eventBox) {
        this.outputEventQueue.addAll(eventBox.outputEventQueue);
        this.inputEventQueue.addAll(eventBox.inputEventQueue);
    }

    public void addOutputEvent(EndPointOutputEvent<UID> event) {
        this.outputEventQueue.add(event);
    }

    public void addInputEvent(EndPointInputEvent<UID> event) {
        this.inputEventQueue.add(event);
    }

    public EndPointInputEvent<UID> pollInputEvent() {
        if (this.inputEventQueue == null || this.inputEventQueue.isEmpty())
            return null;
        return this.inputEventQueue.poll();
    }

    public EndPointOutputEvent<UID> pollOutputEvent() {
        if (this.outputEventQueue == null || this.outputEventQueue.isEmpty())
            return null;
        return this.outputEventQueue.poll();
    }

}
