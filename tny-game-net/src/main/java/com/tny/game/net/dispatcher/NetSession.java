package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.MessageChecker;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID> {

    List<MessageChecker> getCheckers();

    void addChecker(MessageChecker checker);

    void removeChecker(MessageChecker checker);

    void removeChecker(Class<? extends MessageChecker> checkClass);

    default void sendMessage(Protocol protocol, Object body) {
        sendMessage(protocol, ResultCode.SUCCESS, body);
    }

    default void sendMessage(Protocol protocol, ResultCode code, Object body) {
        sendMessage(protocol, code, body, null);
    }

    default void sendMessage(Protocol protocol, ResultCode code, Object body, Integer toMessage) {
        sendMessage(protocol, MessageContent.of(code, body, toMessage));
    }

    void sendMessage(Protocol protocol, MessageContent content);

    void receiveMessage(NetMessage message);

    void resendMessage(int messageID);

    void resendMessages(int fromID);

    void resendMessages(int fromID, int toID);

    boolean hasSendMessage();

    boolean hasReceiveMessage();

    void processSendMessages();

    void processReceiveMessage(MessageDispatcher dispatcher);

    MessageBuilderFactory getMessageBuilderFactory();

    void takeFuture(int id);

}
