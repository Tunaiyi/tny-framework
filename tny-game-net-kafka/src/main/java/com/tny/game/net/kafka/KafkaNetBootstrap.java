package com.tny.game.net.kafka;

import com.tny.game.common.reflect.ObjectUtils;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.Message;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.dispatcher.ClientSession;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.command.MessageCommand;
import com.tny.game.net.dispatcher.RequestSession;
import com.tny.game.net.dispatcher.ServerSession;
import com.tny.game.net.exception.DispatchException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.net.base.CoreResponseCode.*;

/**
 * Created by Kun Yang on 16/8/9.
 */
public class KafkaNetBootstrap {

    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaNetBootstrap.class);

    private AtomicBoolean shutdown = new AtomicBoolean(true);

    private KafkaConsumer<String, KafkaMessage> consumer;

    private KafkaProducer<String, KafkaMessage> producer;

    private volatile boolean change = true;

    private String localSerType;

    private int localSerID;

    private long timeout;

    private Set<String> topics = new CopyOnWriteArraySet<>();

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    private KafkaAppContext appContext;

    private KafkaSessionMap<ClientSession> clientSessionMap = new KafkaSessionMap<>();

    private KafkaSessionMap<ServerSession> serverSessionMap = new KafkaSessionMap<>();

    private Set<Throwable> shutdownExceptions = new CopyOnWriteArraySet<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor(new CoreThreadFactory("KafkaNetBootstrap"));

    public KafkaNetBootstrap(KafkaAppContext context, KafkaProducer<String, KafkaMessage> producer, KafkaConsumer<String, KafkaMessage> consumer) {
        this.appContext = context;
        this.producer = producer;
        this.consumer = consumer;
        List<String> topics = new ArrayList<>();
        context.getServers().stream()
                .map(Topics::requestTopic)
                .forEach(topics::add);
        context.getServers().stream()
                .map(Topics::responseTopic)
                .forEach(topics::add);
        LOGGER.info("KafkaNet 注册 Request 主题: {}", topics);
        this.addTopics(topics);
        this.localSerID = context.getLocalServerID();
        this.localSerType = context.getScopeType();
        this.timeout = context.getPollTimeout();
    }

    public Optional<RequestSession> getClient(String connectSerType, int connectSerID) {
        return Optional.ofNullable(clientSessionMap.get(connectSerType, connectSerID));
    }

    public RequestSession getOrCreateClient(String userGroup, long uid, KafkaServerInfo remoteServer) {
        return getOrCreateClient(remoteServer.getServerType(), remoteServer.getID(), userGroup, uid, null, null);
    }

    public RequestSession getOrCreateClient(KafkaServerInfo remoteServer) {
        return getOrCreateClient(remoteServer.getServerType(), remoteServer.getID(), this.localSerType, this.localSerID, null, null);
    }

    public RequestSession getOrCreateClient(String userGroup, long uid, KafkaServerInfo remoteServer, MessageSignGenerator verifier) {
        return getOrCreateClient(remoteServer.getServerType(), remoteServer.getID(), userGroup, uid, null, verifier);
    }

    public RequestSession getOrCreateClient(String userGroup, long uid, KafkaServerInfo remoteServer, KafkaMessageBuilderFactory messageBuilderFactory) {
        return getOrCreateClient(remoteServer.getServerType(), remoteServer.getID(), userGroup, uid, messageBuilderFactory, null);
    }

    public RequestSession getOrCreateClient(String remoteType, int remoteUID, String userGroup, long uid, KafkaMessageBuilderFactory messageBuilderFactory, MessageSignGenerator verifier) {
        ClientSession session = clientSessionMap.get(remoteType, remoteUID);
        if (session == null) {
            LoginCertificate cer = LoginCertificate.createLogin(uid, uid, userGroup);
            KafkaServerInfo remoteServer = new KafkaServerInfo(remoteType, remoteUID);
            session = KafkaSession.clientSession(producer, cer, remoteServer,
                    ObjectUtils.defaultIfNull(messageBuilderFactory, appContext.getMessageBuilderFactory()),
                    ObjectUtils.defaultIfNull(verifier, appContext.getVerifier()));
            session = clientSessionMap.put(remoteType, remoteUID, session);
            // String topic = Topics.responseTopic(cer);
            // addTopic(topic);
            // LOGGER.info("KafkaNet 注册 Response 主题: {}", topic);
        }
        return session;
    }

    public void start() {
        if (shutdown.compareAndSet(true, false)) {
            executorService.execute(this::loop);
        }
    }

    public void shutdown() throws Throwable {
        if (shutdown.compareAndSet(false, true)) {
            try {
                shutdownLatch.await(3, TimeUnit.MILLISECONDS);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
            for (Throwable throwable : this.shutdownExceptions)
                throw throwable;
        }
    }

    private void loop() {
        while (true) {
            try {
                if (shutdown.get()) {
                    try {
                        consumer.close();
                    } catch (Throwable e) {
                        shutdownExceptions.add(e);
                    }
                    try {
                        producer.close();
                    } catch (Throwable e) {
                        shutdownExceptions.add(e);
                    }
                    this.shutdownLatch.countDown();
                }
                if (topics.isEmpty()) {
                    Thread.sleep(3000);
                } else {
                    Thread.sleep(1000);
                    if (change) {
                        try {
                            this.topics.forEach(t -> {
                                try {
                                    //TODO 当producer 与 consumer 连接的Kafka不同时?
                                    this.producer.send(new ProducerRecord<>(t, "empty", KafkaRequest.BLANK)).get();
                                } catch (InterruptedException | ExecutionException e) {
                                    LOGGER.error("Kafka尝试创建主题失败 {}", topics, e);
                                }
                            });
                            this.consumer.subscribe(new ArrayList<>(topics));
                            LOGGER.info("KafkaNet 开始订阅主题 : {}", topics);
                            this.change = false;
                        } catch (Throwable e) {
                            change = true;
                            LOGGER.error("Kafka订阅主题失败 {}", topics, e);
                        }
                    }
                    ConsumerRecords<String, KafkaMessage> records;
                    try {
                        records = consumer.poll(this.timeout);
                    } catch (Throwable e) {
                        LOGGER.error("Kafka获取消息失败", e);
                        continue;
                    }
                    if (records.isEmpty())
                        continue;
                    for (TopicPartition partition : records.partitions()) {
                        List<ConsumerRecord<String, KafkaMessage>> rs = records.records(partition);
                        LOGGER.debug("ConsumerLoop [{}] 获取 {} 条Records", partition.topic(), rs.size());
                        for (ConsumerRecord<String, KafkaMessage> record : records) {
                            Message message = record.value();
                            if (message == null)
                                continue;
                            try {
                                switch (message.getMessage()) {
                                    case REQUEST:
                                        handle((KafkaRequest) message);
                                        break;
                                    case RESPONSE:
                                        handle((KafkaResponse) message);
                                        break;
                                }
                            } catch (DispatchException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(KafkaRequest request) throws DispatchException {
        LoginCertificate cer = checkTicket(request);
        if (!cer.isLogin())
            throw new DispatchException(UNLOGIN);
        KafkaTicket ticket = request.getTicket();
        ServerSession session = this.serverSessionMap.get(cer.getUserGroup(), cer.getUserID());
        if (session == null) {
            KafkaServerInfo localServer = new KafkaServerInfo(ticket.getRemoteType(), ticket.getRemoteID());
            session = KafkaSession.serverSession(this.producer, localServer, appContext.getMessageBuilderFactory(), appContext.getCheckers());
            session = this.serverSessionMap.put(cer.getUserGroup(), cer.getUserID(), session);
        }
        session.attributes().setAttribute(KafkaAttrKeys.KAFKA_LOGIN_KEY, cer);
        MessageCommand<CommandResult> command = appContext.getMessageDispatcher().dispatch(request, session, appContext);
        if (command != null)
            appContext.getCommandExecutor().submit(session, command);
    }

    private void handle(KafkaResponse response) throws DispatchException {
        ClientSession session = clientSessionMap.get(response.getRemoteType(), response.getRemoteID());
        if (session == null) {
            this.getOrCreateClient(new KafkaServerInfo(response.getRemoteType(), response.getRemoteID()));
            session = clientSessionMap.get(response.getRemoteType(), response.getRemoteID());
        }
        MessageCommand<Void> command = appContext.getMessageDispatcher().dispatch(response, session, appContext);
        if (command != null)
            appContext.getCommandExecutor().submit(session, command);
    }

    private void addTopic(String topic) {
        if (topics.add(topic))
            this.change = true;
    }

    private void addTopics(Collection<String> topics) {
        if (this.topics.addAll(topics))
            this.change = true;
    }

    private LoginCertificate checkTicket(KafkaRequest request) {
        KafkaTicket ticket = request.getTicket();
        KafkaTicketTaker taker = appContext.getTicketTaker();
        if (taker.take(ticket, request.getSign())) {
            return LoginCertificate.createLogin(ticket.getUID(), ticket.getUID(), ticket.getUserGroup());
        } else {
            return LoginCertificate.createUnLogin();
        }
    }

}
