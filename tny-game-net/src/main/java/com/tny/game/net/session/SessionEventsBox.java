package com.tny.game.net.session;

import com.google.common.collect.*;
import com.tny.game.common.context.*;
import com.tny.game.net.message.Message;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

/**
 * Session 输入输出事件箱
 * Created by Kun Yang on 2017/3/23.
 */
public class SessionEventsBox<UID> {

    // private static ConcurrentMap<Long, Reference<SessionEventsBox>> BOX_MAP = new ConcurrentHashMap<>();

    private static final long INIT_DELAY = 3;
    private static final long PERIOD = 3;

    // static {
    //     CommonExecutorService.getScheduledExecutor()
    //             .scheduleAtFixedRate(SessionEventsBox::clearReferences, INIT_DELAY, PERIOD, TimeUnit.MINUTES);
    // }

    /* 附加属性 */
    private Attributes attributes;

    /* 接收队列 */
    private ConcurrentLinkedDeque<SessionInputEvent<UID>> inputEventQueue = new ConcurrentLinkedDeque<>();

    /* 发送队列 */
    private ConcurrentLinkedDeque<SessionOutputEvent<UID>> outputEventQueue = new ConcurrentLinkedDeque<>();

    /* 发送缓存 */
    private CircularFifoQueue<Message<UID>> sentMessageQueue = null;

    private StampedLock sentMessageLock;

    public SessionEventsBox(int cacheMessageSize) {
        if (cacheMessageSize > 0) {
            this.sentMessageQueue = new CircularFifoQueue<>(cacheMessageSize);
            sentMessageLock = new StampedLock();
        }
    }

    public Attributes attributes() {
        if (this.attributes != null)
            return this.attributes;
        synchronized (this) {
            if (this.attributes != null)
                return this.attributes;
            return this.attributes = ContextAttributes.create();
        }
    }

    public void addOutputEvent(SessionOutputEvent<UID> event) {
        this.outputEventQueue.add(event);
    }

    public void addInputEvent(SessionInputEvent<UID> event) {
        this.inputEventQueue.add(event);
    }

    public void addInputEvent(Collection<SessionInputEvent<UID>> events) {
        this.inputEventQueue.addAll(events);
    }

    public void addOutputEvent(Collection<SessionOutputEvent<UID>> events) {
        this.outputEventQueue.addAll(events);
    }

    public boolean hasInputEvent() {
        return !inputEventQueue.isEmpty();
    }

    public boolean hasOutputEvent() {
        return !outputEventQueue.isEmpty();
    }

    public int getInputEventSize() {
        return inputEventQueue.size();
    }

    public int getOutputEventSize() {
        return outputEventQueue.size();
    }

    public void accept(SessionEventsBox<UID> eventBox) {
        this.outputEventQueue.addAll(eventBox.outputEventQueue);
        this.inputEventQueue.addAll(eventBox.inputEventQueue);
    }

    public SessionInputEvent<UID> pollInputEvent() {
        Queue<SessionInputEvent<UID>> inputEventQueue = this.inputEventQueue;
        if (inputEventQueue == null || inputEventQueue.isEmpty())
            return null;
        return inputEventQueue.poll();
    }

    public SessionOutputEvent<UID> pollOutputEvent() {
        Queue<SessionOutputEvent<UID>> outputEventQueue = this.outputEventQueue;
        if (outputEventQueue == null || outputEventQueue.isEmpty())
            return null;
        return outputEventQueue.poll();
    }

    public List<Message<UID>> getSentMessage(Range<Integer> range) {
        if (this.sentMessageQueue == null)
            return ImmutableList.of();
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return this.sentMessageQueue.stream()
                    .filter(e -> range.contains(e.getId()))
                    .collect(Collectors.toList());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public Message<UID> getSentMessageByToID(int messageId) {
        if (this.sentMessageQueue == null)
            return null;
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return this.sentMessageQueue.stream()
                    .filter(message -> message.getHeader().getId() == messageId)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void addSentMessage(Message<UID> message) {
        if (this.sentMessageQueue != null) {
            StampedLock lock = this.sentMessageLock;
            long stamp = lock.writeLock();
            try {
                this.sentMessageQueue.add(message);
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    public static <UID> SessionEventsBox<UID> create(int cacheMessageSize) {
        return new SessionEventsBox<>(cacheMessageSize);
    }

}
