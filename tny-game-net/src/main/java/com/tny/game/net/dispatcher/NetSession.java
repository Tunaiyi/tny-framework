package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID> {

    // List<MessageChecker> getCheckers();
    //
    // void addChecker(MessageChecker checker);
    //
    // void removeChecker(MessageChecker checker);
    //
    // void removeChecker(Class<? extends MessageChecker> checkClass);
    //
    // MessageSignGenerator getMessageSignGenerator();

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

    void disconnect();

    void login(LoginCertificate<UID> loginInfo);
}
