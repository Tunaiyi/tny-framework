package com.tny.game.net.transport;

import static com.tny.game.common.utils.ObjectAide.as;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class EmptySendContext<UID> implements SendContext<UID> {

    private static final SendContext EMPTY_SEND_CONTEXT = new EmptySendContext();

    public static <UID> SendContext<UID> empty() {
        return as(EMPTY_SEND_CONTEXT);
    }

    private EmptySendContext() {
    }

    @Override
    public MessageSendFuture<UID> getSendFuture() {
        return null;
    }

    @Override
    public RespondFuture<UID> getRespondFuture() {
        return null;
    }

    /**
     * @return 是否有发送 Future
     */
    @Override
    public boolean isHasSendFuture() {
        return false;
    }

    /**
     * @return 是否有响应 Future
     */
    @Override
    public boolean isHasRespondFuture() {
        return false;
    }

    /**
     * @return 是否有 Future
     */
    @Override
    public boolean isHasFuture() {
        return false;
    }

}
