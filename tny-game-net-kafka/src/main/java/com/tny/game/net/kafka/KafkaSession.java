package com.tny.game.net.kafka;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.utils.DateTimeHelper;
import com.tny.game.log.NetLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.dispatcher.AbstractServerSession;
import com.tny.game.net.dispatcher.ClientSession;
import com.tny.game.net.message.MessageAction;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.message.MessageFutureHolder;
import com.tny.game.net.dispatcher.MessageSendFuture;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Response;
import com.tny.game.net.dispatcher.ServerSession;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kun Yang on 16/8/9.
 */
class KafkaSession extends AbstractServerSession implements ClientSession {

    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaSession.class);

    private Producer<String, KafkaMessage> producer;

    private MessageFutureHolder futureHolder = new MessageFutureHolder();

    private AtomicInteger responseNumberCreator = new AtomicInteger();
    private AtomicInteger requestIDCreator = new AtomicInteger();
    private AtomicBoolean online = new AtomicBoolean(true);

    private KafkaServerInfo serverInfo;

    private SessionModel model;

    private MessageSignGenerator verifier;

    private String loginKey;

    private String topic;

    public static ServerSession serverSession(Producer<String, KafkaMessage> producer, KafkaServerInfo localServer, KafkaMessageBuilderFactory messageBuilderFactory, List<ControllerChecker> checkers) {
        return new KafkaSession(SessionModel.SERVER, producer, null, localServer, messageBuilderFactory, checkers, null);
    }

    public static ClientSession clientSession(Producer<String, KafkaMessage> producer, LoginCertificate certificate, KafkaServerInfo removeServer, KafkaMessageBuilderFactory messageBuilderFactory, MessageSignGenerator verifier) {
        return new KafkaSession(SessionModel.CLIENT, producer, certificate, removeServer, messageBuilderFactory, null, verifier);
    }

    private KafkaSession(SessionModel model, Producer<String, KafkaMessage> producer, LoginCertificate certificate, KafkaServerInfo serverInfo, KafkaMessageBuilderFactory messageBuilderFactory, List<ControllerChecker> checkers, MessageSignGenerator verifier) {
        super(LoginCertificate.createUnLogin());
        this.model = model;
        this.producer = producer;
        this.messageBuilderFactory = messageBuilderFactory;
        this.checkers = checkers != null ? ImmutableList.copyOf(checkers) : ImmutableList.of();
        this.serverInfo = serverInfo;
        this.verifier = verifier;
        if (certificate != null && certificate.isLogin())
            this.setConnect(certificate);
    }

    public void setConnect(LoginCertificate certificate) {
        if (model == SessionModel.CLIENT) {
            super.login(certificate);
            LoginCertificate cer = this.certificate;
            if (cer.isLogin()) {
                this.loginKey = DateTimeHelper.time2Second(new DateTime(cer.getLoginAt())) + "";
                this.topic = Topics.requestTopic(this.serverInfo);
            }
        }
    }

    @Override
    public void login(LoginCertificate certificate) {
        if (model == SessionModel.SERVER) {
            super.login(certificate);
            LoginCertificate cer = this.certificate;
            if (cer.isLogin()) {
                this.loginKey = DateTimeHelper.time2Second(new DateTime(cer.getLoginAt())) + "";
                this.topic = Topics.responseTopic(cer);
            }
        }
    }

    @Override
    protected int createResponseNumber() {
        return responseNumberCreator.incrementAndGet();
    }

    @Override
    protected Optional<MessageSendFuture> write(Object data) {
        return write(data, null);
    }

    protected Optional<MessageSendFuture> write(Object data, MessageFuture<?> future) {
        if (!(data instanceof KafkaMessage)) {
            LOGGER.error("KafkaSession 无法发送 {} 类型的消息", data.getClass());
            return Optional.empty();
        }
        try {
            KafkaMessage message = (KafkaMessage) data;
            String topic = getMessageTopic();
            String key = "";
            ExceptionUtils.checkArgument(this.model.isCanSend(message), "SessionModel {} 无法发送 {} 信息", this.model, message.getMessage());
            if (data instanceof Response) {
                Response response = (Response) data;
                key = Topics.messageKey(topic, loginKey, response.getNumber());
            } else if (data instanceof Request) {
                Request request = (Request) data;
                key = Topics.messageKey(topic, loginKey, request.getID());
                if (future != null)
                    future.setRequest(request)
                            .setSession(this);
            }
            ProducerRecord<String, KafkaMessage> record = new ProducerRecord<>(topic, key, message);
            if (future != null)
                producer.send(record, (r, e) -> {
                    if (e == null)
                        this.putFuture(future);
                    else
                        future.cancel(true);
                });
            else
                producer.send(record);
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("#Session#sendMessage 异常", e);
        }
        return Optional.empty();
    }

    @Override
    public void disconnect() {
        online.compareAndSet(true, false);
    }

    @Override
    public String getHostName() {
        return "";
    }

    @Override
    public boolean isConnect() {
        return online.get();
    }

    @Override
    public boolean isOnline() {
        return online.get();
    }

    private String getMessageTopic() {
        if (topic == null)
            throw new IllegalArgumentException("topic is null");
        return topic;
    }

    @Override
    public Optional<MessageSendFuture> request(Protocol protocol, Object... params) {
        return this.request(protocol, (MessageFuture<?>) null, params);
    }

    @Override
    public Optional<MessageSendFuture> request(Protocol protocol, MessageAction<?> action, long timeout, Object... params) {
        MessageFuture<?> future = new MessageFuture<>(action, timeout);
        return this.request(protocol, future, params);
    }

    @Override
    public Optional<MessageSendFuture> request(Protocol protocol, MessageFuture<?> future, Object... params) {
        Request request = this.getMessageBuilderFactory()
                .newRequestBuilder(this)
                .setID(this.requestIDCreator.getAndIncrement())
                .setProtocol(protocol)
                .setRequestVerifier(verifier)
                .addParameter(params)
                .build();
        NetLogger.log(this, request);
        if (future != null) {
            future.setRequest(request)
                    .setSession(this);
        }
        return this.write(request, future);
    }

    @Override
    public MessageFuture<?> takeFuture(int id) {
        return futureHolder.takeFuture(id);
    }

    public KafkaServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public void putFuture(MessageFuture<?> future) {
        futureHolder.putFuture(future);
    }

    @Override
    public void clearFuture() {
        futureHolder.clearFuture();
    }

}
