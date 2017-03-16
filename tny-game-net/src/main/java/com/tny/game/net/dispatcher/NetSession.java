package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.session.Session;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID> {

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

    void offline(boolean invalid);

    default void offline() {
        offline(false);
    }

    boolean relogin(NetSession<UID> uid);

    void login(LoginCertificate<UID> loginInfo);

    /**
     * 将当前的session 转换到指定session
     * @param session 转换的目标session
     * @param <T>
     * @return
     */
    boolean exchange(NetSession<UID> session);

}
