package com.tny.game.net.session;


import com.google.common.collect.Range;
import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;

import java.util.List;

/**
 * 重发缓存消息的消息
 * <p>
 * Created by Kun Yang on 2017/3/26.
 */
public class ResendMessage<UID> {

    private Range<Integer> resendRange;

    private volatile CommonStageableFuture<ResendResult<UID>> sendFuture;

    public static <T> ResendMessage<T> of(int messageID) {
        return new ResendMessage(Range.singleton(messageID));
    }

    public static <T> ResendMessage<T> from(int fromMessageID) {
        return new ResendMessage(Range.atLeast(fromMessageID));
    }

    public static <T> ResendMessage<T> fromTo(int fromMessageID, int toMessageID) {
        return new ResendMessage(Range.closed(fromMessageID, toMessageID));
    }

    public static <T> ResendMessage<T> ofRange(Range<Integer> resendRange) {
        return new ResendMessage(resendRange);
    }

    private ResendMessage(Range<Integer> resendRange) {
        this.resendRange = resendRange;
    }

    public Range<Integer> getResendRange() {
        return resendRange;
    }

    private StageableFuture<ResendResult<UID>> createSendFuture() {
        if (this.sendFuture != null)
            return this.sendFuture;
        synchronized (this) {
            if (this.sendFuture == null)
                this.sendFuture = new CommonStageableFuture<>();
        }
        return this.sendFuture;
    }

    public boolean isHasFuture() {
        return this.sendFuture != null;
    }

    public StageableFuture<ResendResult<UID>> sendFuture() {
        return createSendFuture();
    }

    void resendSuccess(Tunnel<UID> tunnel, List<Message<UID>> messages) {
        if (this.sendFuture != null) {
            if (!sendFuture.isDone()) {
                sendFuture.future().complete(new ResendResult<>(tunnel, messages));
            }
        }
    }

    void resendFailed(Throwable cause) {
        if (this.sendFuture != null) {
            if (!sendFuture.isDone()) {
                sendFuture.future().completeExceptionally(cause);
            }
        }
    }


}
