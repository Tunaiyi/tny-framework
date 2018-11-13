package com.tny.game.net.transport;

import com.tny.game.net.message.MessageSubject;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageContext<UID> extends SendContext<UID>, MessageSubject {

    Object getAttachment();

    boolean isHasAttachment();

    MessageContext<UID> setAttachment(Object attachment);

    MessageContext<UID> setBody(Object attachment);

    MessageContext<UID> willSendFuture(Consumer<MessageSendFuture<UID>> consumer);

    MessageContext<UID> willSendFuture();

}
