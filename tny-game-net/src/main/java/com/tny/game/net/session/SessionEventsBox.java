package com.tny.game.net.session;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.event.SessionInputEvent;
import com.tny.game.net.session.event.SessionOutputEvent;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
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

    private long sessionID;

    private volatile int messageIDCounter = 0;

    /* 附加属性 */
    private Attributes attributes;

    /* 接收队列 */
    private ConcurrentLinkedDeque<SessionInputEvent<UID>> inputEventQueue = new ConcurrentLinkedDeque<>();

    /* 发送队列 */
    private ConcurrentLinkedDeque<SessionOutputEvent<UID>> outputEventQueue = new ConcurrentLinkedDeque<>();

    /* 发送缓存 */
    private CircularFifoQueue<Message<UID>> sentMessageQueue = null;

    private StampedLock sentMessageLock;

    public SessionEventsBox(long sessionID, int cacheMessageSize) {
        this.sessionID = sessionID;
        if (cacheMessageSize > 0) {
            this.sentMessageQueue = new CircularFifoQueue<>(cacheMessageSize);
            sentMessageLock = new StampedLock();
        }
    }

    public long getSessionID() {
        return sessionID;
    }

    public Attributes getAttributes() {
        return this.attributes;
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

    public int allotMessageID() {
        return ++messageIDCounter;
    }

    public void addOutputEvent(SessionOutputEvent event) {
        this.outputEventQueue.add(event);
    }

    public void addInputEvent(SessionInputEvent event) {
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
                    .filter(e -> range.contains(e.getID()))
                    .collect(Collectors.toList());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public Message<UID> getSentMessageByToID(int toMessageID) {
        if (this.sentMessageQueue == null)
            return null;
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return this.sentMessageQueue.stream()
                    .filter(message -> message.getToMessage() == toMessageID)
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

    @SuppressWarnings("unchecked")
    // public static <UID> SessionEventsBox<UID> getEventBox(Session<UID> session) {
    //     Reference<SessionEventsBox> boxReference = BOX_MAP.get(session.getID());
    //     if (boxReference == null)
    //         return null;
    //     return boxReference.get();
    // }

    public static <UID> SessionEventsBox<UID> create(Session<UID> session, int cacheMessageSize) {
        // SessionEventsBox<UID> box = getEventBox(session);
        // if (box != null)
        //     return box;
        return new SessionEventsBox<>(session.getID(), cacheMessageSize);
        // Reference<SessionEventsBox> boxReference = new WeakReference<>(box);
        // BOX_MAP.put(box.getSessionID(), boxReference);
        // return box;
    }


    // public static void removeEventBox(SessionEventsBox<?> box) {
    //     BOX_MAP.remove(box.getSessionID());
    // }

    // public static void removeEventBox(Session<?> session) {
    //     BOX_MAP.remove(session.getID());
    // }

    // private static void clearReferences() {
    //     for (Entry<Long, Reference<SessionEventsBox>> entry : BOX_MAP.entrySet()) {
    //         Reference<SessionEventsBox> reference = entry.getValue();
    //         if (reference.get() == null)
    //             BOX_MAP.remove(entry.getKey());
    //     }
    // }


}
