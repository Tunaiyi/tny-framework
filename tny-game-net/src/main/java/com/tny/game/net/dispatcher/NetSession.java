package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetMessage;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.MessageChecker;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession extends Session {

    List<MessageChecker> getCheckers();

    void addChecker(MessageChecker checker);

    void removeChecker(MessageChecker checker);

    void removeChecker(Class<? extends MessageChecker> checkClass);

    void sendMessage(Protocol protocol, Object body);

    void sendMessage(Protocol protocol, Object body, ResultCode code);

    void sendMessage(Protocol protocol, Object body, ResultCode code, int toMessage);

    default void sendMessage(Protocol protocol, MessageContent content) {
        sendMessage(protocol, content, 30000);
    }

    void sendMessage(Protocol protocol, MessageContent content, long timeout);

    boolean hasSendMessage();

    Iterable<MessageCapsule> takeSendMessages();

    void resendMessage(int fromID);

    void resendMessage(int fromID, int toID);

    void pullReceiveMessage(NetMessage message);

    NetMessage pollReceiveMessage();

    MessageBuilderFactory getMessageBuilderFactory();

    void takeFuture(int id);

}
