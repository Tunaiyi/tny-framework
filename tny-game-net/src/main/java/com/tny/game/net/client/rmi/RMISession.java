package com.tny.game.net.client.rmi;


import com.tny.game.common.context.Attributes;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.session.Session;
import org.joda.time.DateTime;

public class RMISession<UID> implements Session<UID> {

    @Override
    public UID getUID() {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public DateTime getLoginAt() {
        return null;
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public String getHostName() {
        return null;
    }

    @Override
    public Attributes attributes() {
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean isInvalided() {
        return false;
    }

    @Override
    public LoginCertificate<UID> getCertificate() {
        return null;
    }

    @Override
    public void offline(boolean invalid) {

    }

    @Override
    public long getOfflineTime() {
        return 0;
    }

    @Override
    public long getLastReceiveTime() {
        return 0;
    }

    @Override
    public void sendMessage(Protocol protocol, MessageContent content) {

    }

    @Override
    public void receiveMessage(Message<UID> message) {

    }

    @Override
    public void resendMessage(int messageID) {

    }

    @Override
    public void resendMessages(int fromID) {

    }

    @Override
    public void resendMessages(int fromID, int toID) {

    }
}