package com.tny.game.net.netty;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageSentHandler;
import com.tny.game.net.session.event.SessionSendEvent;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;

/**
 * Created by Kun Yang on 2017/3/22.
 */
public class NettySessionSendEvent extends SessionSendEvent {

    private ChannelPromise promise;

    @SuppressWarnings("unchecked")
    public NettySessionSendEvent(NettySession session, Message message, boolean sent, SessionEventType eventType, MessageSentHandler<?> sentHandler) {
        super(message, sent, eventType, sentHandler);
        if (sent || sentHandler != null) {
            promise = new DefaultChannelPromise(session.getChannel());
            if (sentHandler != null)
                promise.addListener(f -> {
                    if (f.isSuccess())
                        this.getSentHandler().onSent(session, message);
                });
        }
    }

    public ChannelPromise getPromise() {
        return promise;
    }

}
