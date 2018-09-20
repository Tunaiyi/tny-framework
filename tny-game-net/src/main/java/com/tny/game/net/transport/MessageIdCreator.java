package com.tny.game.net.transport;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-17 23:50
 */
public class MessageIdCreator {

    public static final long TYPE_MESSAGE_ID_MASK = 1;

    public static final int TUNNEL_MESSAGE_ID_MARK = 0;
    public static final int SESSION_MESSAGE_ID_MARK = 1;

    public static final long MESSAGE_MAX_ID = Long.MAX_VALUE >> 1;

    private volatile AtomicLong idCreator;
    private int mark;

    public MessageIdCreator(int mark) {
        this.idCreator = new AtomicLong(0);
        this.mark = mark;
    }

    private AtomicLong getIdCreator() {
        if (this.idCreator != null) {
            return this.idCreator;
        }
        synchronized (this) {
            if (this.idCreator != null) {
                return this.idCreator;
            }
            this.idCreator = new AtomicLong(0);
        }
        return this.idCreator;
    }

    public long createId() {
        AtomicLong idCreator = getIdCreator();
        while (true) {
            long id = idCreator.incrementAndGet();
            if (id < 0 || id > MESSAGE_MAX_ID) {
                idCreator.compareAndSet(id, 0);
                continue;
            }
            return (id << 1) | mark;
        }
    }

    public static int getMark(long id) {
        return (int) (id & TYPE_MESSAGE_ID_MASK);
    }

    public static boolean isMark(long id, int mark) {
        return (id & TYPE_MESSAGE_ID_MASK) == mark;
    }

}

