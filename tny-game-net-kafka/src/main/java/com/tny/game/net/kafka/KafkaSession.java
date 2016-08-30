package com.tny.game.net.kafka;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.utils.DateTimeHelper;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.checker.RequestVerifier;
import com.tny.game.net.dispatcher.AbstractServerSession;
import com.tny.game.net.dispatcher.ClientSession;
import com.tny.game.net.dispatcher.MessageAction;
import com.tny.game.net.dispatcher.MessageFuture;
import com.tny.game.net.dispatcher.NetFuture;
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

    private AtomicInteger responseNumberCreator = new AtomicInteger();
    private AtomicInteger requestIDCreator = new AtomicInteger();
    private AtomicBoolean online = new AtomicBoolean(true);

    private KafkaServerInfo serverInfo;

    private SessionModel model;

    private RequestVerifier verifier;

    private String loginKey;

    private String topic;

    public static ServerSession serverSession(Producer<String, KafkaMessage> producer, KafkaServerInfo localServer, KafkaMessageBuilderFactory messageBuilderFactory, List<RequestChecker> checkers) {
        return new KafkaSession(SessionModel.SERVER, producer, null, localServer, messageBuilderFactory, checkers, null);
    }

    public static ClientSession clientSession(Producer<String, KafkaMessage> producer, LoginCertificate certificate, KafkaServerInfo removeServer, KafkaMessageBuilderFactory messageBuilderFactory, RequestVerifier verifier) {
        return new KafkaSession(SessionModel.CLIENT, producer, certificate, removeServer, messageBuilderFactory, null, verifier);
    }

    private KafkaSession(SessionModel model, Producer<String, KafkaMessage> producer, LoginCertificate certificate, KafkaServerInfo serverInfo, KafkaMessageBuilderFactory messageBuilderFactory, List<RequestChecker> checkers, RequestVerifier verifier) {
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
                this.loginKey = DateTimeHelper.time2SecondLong(new DateTime(cer.getLoginAt())) + "";
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
                this.loginKey = DateTimeHelper.time2SecondLong(new DateTime(cer.getLoginAt())) + "";
                this.topic = Topics.responseTopic(cer);
            }
        }
    }

    @Override
    protected int createResponseNumber() {
        return responseNumberCreator.incrementAndGet();
    }

    @Override
    protected Optional<NetFuture> write(Object data) {
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
                Topics.messageKey(topic, loginKey, response.getNumber());
            } else if (data instanceof Request) {
                Request request = (Request) data;
                Topics.messageKey(topic, loginKey, request.getID());
            }
            producer.send(new ProducerRecord<>(topic, key, message));
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
    public Optional<NetFuture> request(Protocol protocol, Object... params) {
        Request request = this.getMessageBuilderFactory()
                .newRequestBuilder(this)
                .setID(this.requestIDCreator.getAndIncrement())
                .setProtocol(protocol)
                .setRequestVerifier(verifier)
                .addParameter(params)
                .build();
        CoreLogger.log(this, request);
        return this.write(request);
    }

    @Override
    public Optional<NetFuture> request(Protocol protocol, MessageAction<?> action, Object... params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<NetFuture> request(Protocol protocol, MessageFuture<?> future, Object... params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageFuture<?> takeFuture(int id) {
        return null;
    }

    public KafkaServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public void putFuture(MessageFuture<?> future) {
    }

    @Override
    public void clearFuture() {
    }

}