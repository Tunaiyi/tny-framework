package com.tny.game.net.session;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttributeUtils;

/**
 * Session推送选项
 * Created by Kun Yang on 16/5/17.
 */
public enum SessionPushOption {

    NO_PUSH(false, false),

    PUSH(true, false),

    PUSH_THEN_THROW(false, true),

    //
    ;

    SessionPushOption(boolean push, boolean throwable) {
        this.push = push;
        this.throwable = throwable;
    }

    private boolean push;

    private boolean throwable;

    public boolean isPush() {
        return push;
    }

    public boolean isThrowable() {
        return throwable;
    }

    public static final AttrKey<SessionPushOption> SESSION_PUSH_OPTION = AttributeUtils.key(SessionPushOption.class, "SESSION_PUSH_OPTION");

}
