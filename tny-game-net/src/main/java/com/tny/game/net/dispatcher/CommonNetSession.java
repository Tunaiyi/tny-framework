package com.tny.game.net.dispatcher;

import com.google.common.collect.Range;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Message;
import com.tny.game.net.base.NetMessage;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.MessageChecker;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public abstract class CommonNetSession implements NetSession {

    protected LoginCertificate certificate;

    protected List<MessageChecker> checkers = new CopyOnWriteArrayList<>();

    private volatile transient Attributes attributes;

    protected MessageBuilderFactory messageBuilderFactory;

    private volatile ConcurrentLinkedDeque<Message> receiveMessageQueue;

    private volatile ConcurrentLinkedDeque<MessageOrder> sendMessageQueue;

    private CircularFifoQueue<MessageCapsule> sentMessageCache;

    private volatile HashMap<Integer, MessageFuture<?>> futureMap;

    public CommonNetSession(int cacheMessageSize) {
        if (cacheMessageSize > 0)
            this.sentMessageCache = new CircularFifoQueue<>(cacheMessageSize);
    }

    @Override
    public long getUID() {
        return certificate.getUserID();
    }

    @Override
    public String getGroup() {
        return certificate.getUserGroup();
    }

    @Override
    public long getLoginAt() {
        return certificate.getLoginAt();
    }

    @Override
    public boolean isLogin() {
        return this.certificate != null && this.certificate.isLogin();
    }

    @Override
    public Attributes attributes() {
        if (this.attributes != null)
            return this.attributes;
        synchronized (this) {
            if (this.attributes != null)
                return this.attributes;
            return this.attributes = ContextAttributes.create();
        }
    }

    @Override
    public LoginCertificate getCertificate() {
        return certificate;
    }

    @Override
    public List<MessageChecker> getCheckers() {
        return Collections.unmodifiableList(this.checkers);
    }

    @Override
    public void addChecker(MessageChecker checker) {
        this.checkers.add(checker);
    }

    @Override
    public void removeChecker(MessageChecker checker) {
        this.checkers.remove(checker);
    }

    @Override
    public void removeChecker(Class<? extends MessageChecker> checkClass) {
        for (MessageChecker checker : checkers) {
            if (checkClass.isInstance(checker))
                checkers.remove(checker);
        }
    }

    @Override
    public void sendMessage(Protocol protocol, Object body) {

    }

    @Override
    public void sendMessage(Protocol protocol, Object body, ResultCode code) {

    }

    @Override
    public void sendMessage(Protocol protocol, Object body, ResultCode code, int toMessage) {

    }

    @Override
    public String getHostName() {
        return null;
    }

    @Override
    public void sendMessage(Protocol protocol, MessageContent content, long timeout) {

    }

    @Override
    public void resendMessage(int fromID) {
        this.sendMessageQueue.push(new MessageOrder<>(Range.atLeast(fromID), MessageOrderType.RESEND));
    }

    @Override
    public void resendMessage(int fromID, int toID) {
        this.sendMessageQueue.push(new MessageOrder<>(Range.closed(fromID, toID), MessageOrderType.RESEND));
    }

    @Override
    public Iterable<Message> takeSendMessages() {
        return null;
    }

    @Override
    public void pullReceiveMessage(NetMessage message) {

    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public NetMessage pollReceiveMessage() {
        return null;
    }

    @Override
    public MessageBuilderFactory getMessageBuilderFactory() {
        return null;
    }

    @Override
    public void takeFuture(int id) {

    }

}
