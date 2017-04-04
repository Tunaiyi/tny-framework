package com.tny.game.net.session;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public class SessionUtils {

    private static final AtomicLong SESSION_ID_CREATOR = new AtomicLong(0);

    public static long newSessionID() {
        return SESSION_ID_CREATOR.incrementAndGet();
    }

}
