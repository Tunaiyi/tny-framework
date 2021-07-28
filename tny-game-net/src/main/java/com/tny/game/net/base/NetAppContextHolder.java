package com.tny.game.net.base;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/26 12:16 上午
 */
public final class NetAppContextHolder {

    private static NetAppContext CONTEXT = null;

    private NetAppContextHolder() {
    }

    public static synchronized void register(NetAppContext context) {
        if (CONTEXT == null) {
            CONTEXT = context;
        } else {
            throw new IllegalArgumentException(format("NetAppContext registered, {}", CONTEXT));
        }
    }

    public static NetAppContext getContext() {
        return CONTEXT;
    }

}
