package com.tny.game.net.tunnel;

import com.tny.game.net.session.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Session 输入输出事件箱
 * Created by Kun Yang on 2017/3/23.
 */
public class MessageEventsBox<UID> {

    // private static ConcurrentMap<Long, Reference<SessionEventsBox>> BOX_MAP = new ConcurrentHashMap<>();

    private static final long INIT_DELAY = 3;
    private static final long PERIOD = 3;

    public static <UID> MessageEventsBox<UID> create() {
        return new MessageEventsBox<>();
    }

    // static {
    //     CommonExecutorService.getScheduledExecutor()
    //             .scheduleAtFixedRate(SessionEventsBox::clearReferences, INIT_DELAY, PERIOD, TimeUnit.MINUTES);
    // }

    /* 接收队列 */
    private Deque<MessageInputEvent<UID>> inputEventQueue = new ConcurrentLinkedDeque<>();

    /* 发送队列 */
    private Deque<MessageOutputEvent<UID>> outputEventQueue = new ConcurrentLinkedDeque<>();

    public MessageEventsBox() {
    }

    void addOutputEvent(MessageOutputEvent<UID> event) {
        this.outputEventQueue.add(event);
    }

    void addInputEvent(MessageInputEvent<UID> event) {
        this.inputEventQueue.add(event);
    }

    public boolean isHasInputEvent() {
        return !inputEventQueue.isEmpty();
    }

    public boolean isHasOutputEvent() {
        return !outputEventQueue.isEmpty();
    }

    public int getInputEventSize() {
        return inputEventQueue.size();
    }

    public int getOutputEventSize() {
        return outputEventQueue.size();
    }

    // public void accept(MessageEventsBox<UID> eventBox) {
    //     this.outputEventQueue.addAll(eventBox.outputEventQueue);
    //     this.inputEventQueue.addAll(eventBox.inputEventQueue);
    // }

    public MessageInputEvent<UID> pollInputEvent() {
        Queue<MessageInputEvent<UID>> inputEventQueue = this.inputEventQueue;
        if (inputEventQueue == null || inputEventQueue.isEmpty())
            return null;
        return inputEventQueue.poll();
    }

    public MessageOutputEvent<UID> pollOutputEvent() {
        Queue<MessageOutputEvent<UID>> outputEventQueue = this.outputEventQueue;
        if (outputEventQueue == null || outputEventQueue.isEmpty())
            return null;
        return outputEventQueue.poll();
    }

}
