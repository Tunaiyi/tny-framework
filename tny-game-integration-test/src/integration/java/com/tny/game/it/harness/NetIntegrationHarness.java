/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.it.harness;

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.url.*;
import com.tny.game.net.application.*;
import com.tny.game.net.application.configuration.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import org.slf4j.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

/**
 * 进程内真实 TCP 集成测试夹具（add-integration-testing design D2/D3）。
 * <p>
 * 装配配方复刻生产 starter {@code ImportNetBootstrapDefinitionRegistrar#registerChannelMaker} 的去 Spring 版：
 * 以 {@link UnitLoader#register(String, Object)} 两参路径注册带 {@code it} 前缀的专属单元名（不占用
 * 生产默认名，避免同 JVM 全局表冲突），随后手工驱动 guide 与帧编解码器的 {@code prepareStart()}，
 * 绕开"完整 unit 扫描装配链"的历史卡点（{@code GuideLifecycleTest} 注释所述）。
 * <p>
 * 服务端绑定 {@code 127.0.0.1} 上由 {@link ServerSocket} 探得的临时端口（端口 0 的回读在
 * {@link NettyServerGuide} 公共 API 不可见，故用选空重试替代），bind 失败自动换端口重试。
 */
public final class NetIntegrationHarness implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetIntegrationHarness.class);

    private static final String HOST = "127.0.0.1";

    private static final int BIND_ATTEMPTS = 5;

    /**
     * TCP 报文体编解码实现（specs R4"多实现参数化往返"的参数域）。
     */
    public enum BodyCodecKind {

        TYPE_PROTOBUF("itTypeProtobufBodyCodec", TypeProtobufMessageBodyCodec::new),
        PROTO_EX("itProtoExBodyCodec", ProtoExMessageBodyCodec::new);

        private final String unitName;

        private final Supplier<MessageBodyCodec<?>> factory;

        BodyCodecKind(String unitName, Supplier<MessageBodyCodec<?>> factory) {
            this.unitName = unitName;
            this.factory = factory;
        }

        public String unitName() {
            return unitName;
        }
    }

    private static final String MESSAGE_FACTORY = "itMessageFactory";

    private static final String CONTACT_FACTORY = "itContactFactory";

    private static final String SESSION_FACTORY = "itSessionFactory";

    private static final String MESSAGE_DISPATCHER = "itMessageDispatcher";

    private static final String COMMAND_EXECUTOR_FACTORY = "itCommandExecutorFactory";

    private static final String RPC_FORWARDER = "itRpcForwarder";

    private static final String RPC_MONITOR = "itRpcMonitor";

    private static final String ID_GENERATOR = "itNetIdGenerator";

    private static final String SERVER_TUNNEL_FACTORY = "itServerTunnelFactory";

    private static final String MESSAGE_HEADER_CODEC = "itMessageHeaderCodec";

    private static final String CODEC_VERIFIER = "itCrc64CodecVerifier";

    private static final String CODEC_CRYPTO = "itXOrCodecCrypto";

    private static final NetAppContext APP_CONTEXT = new DefaultNetAppContext();

    /**
     * 单元表为 JVM 级静态，注册只执行一次（带 it 前缀名与生产注册面隔离，多 harness 实例共享安全）。
     */
    private static final AtomicBoolean COMMON_UNITS_REGISTERED = new AtomicBoolean(false);

    /**
     * handler 工厂单元按 harness 实例注册（sink 各异），序号保证全局表键不冲突。
     */
    private static final AtomicInteger HANDLER_UNIT_SEQ = new AtomicInteger();

    private final NettyServerGuide serverGuide;

    private final NettyClientGuide clientGuide;

    private final InetSocketAddress serverAddress;

    private final List<NetTunnel> clientTunnels = new CopyOnWriteArrayList<>();

    private final BlockingQueue<NetMessage> serverInbound;

    private final BlockingQueue<NetMessage> clientInbound;

    private final AtomicReference<InboundHandler> serverHandler;

    private final AtomicReference<InboundHandler> clientHandler;

    private NetIntegrationHarness(NettyServerGuide serverGuide, NettyClientGuide clientGuide, int port,
            BlockingQueue<NetMessage> serverInbound, BlockingQueue<NetMessage> clientInbound,
            AtomicReference<InboundHandler> serverHandler, AtomicReference<InboundHandler> clientHandler) {
        this.serverGuide = serverGuide;
        this.clientGuide = clientGuide;
        this.serverAddress = new InetSocketAddress(HOST, port);
        this.serverInbound = serverInbound;
        this.clientInbound = clientInbound;
        this.serverHandler = serverHandler;
        this.clientHandler = clientHandler;
    }

    public static NetIntegrationHarness startProtobuf() throws Exception {
        return start(BodyCodecKind.TYPE_PROTOBUF);
    }

    /**
     * 起服（真实 TCP 监听）并备妥客户端引导器；bind 竞争窗口下自动换临时端口重试。
     */
    public static NetIntegrationHarness start(BodyCodecKind kind) throws Exception {
        ensureCommonUnitsRegistered();
        registerBodyCodec(kind);

        BlockingQueue<NetMessage> serverInbound = new LinkedBlockingQueue<>();
        BlockingQueue<NetMessage> clientInbound = new LinkedBlockingQueue<>();
        AtomicReference<InboundHandler> serverHandler = new AtomicReference<>();
        AtomicReference<InboundHandler> clientHandler = new AtomicReference<>();

        Exception lastFailure = null;
        for (int attempt = 1; attempt <= BIND_ATTEMPTS; attempt++) {
            int port = freePort();
            NettyNetServerBootstrapSetting serverSetting = serverSetting(port, kind,
                    registerInboundHandlerFactory(true, serverHandler, serverInbound));
            NettyServerGuide serverGuide = new NettyServerGuide(APP_CONTEXT, serverSetting, channelMaker(serverSetting));
            serverGuide.prepareStart();
            serverGuide.open();
            if (serverGuide.isBound()) {
                NettyNetClientBootstrapSetting clientSetting = clientSetting(kind,
                        registerInboundHandlerFactory(false, clientHandler, clientInbound));
                NettyClientGuide clientGuide = new NettyClientGuide(APP_CONTEXT, clientSetting, channelMaker(clientSetting));
                clientGuide.prepareStart();
                return new NetIntegrationHarness(serverGuide, clientGuide, port,
                        serverInbound, clientInbound, serverHandler, clientHandler);
            }
            lastFailure = new IllegalStateException("server guide failed to bind on port " + port + " (attempt " + attempt + ")");
            LOGGER.warn("harness bind retry: {}", lastFailure.getMessage());
            serverGuide.close();
        }
        throw lastFailure;
    }

    /**
     * client-only 形态（add-starter-net-integration-tests design D6）：仅备妥客户端引导器不起服——
     * 服务端由 Spring 官方装配启动的场景使用，消息编解码/帧格式与完整 harness 同源。
     */
    public static NetIntegrationHarness startClientOnly(BodyCodecKind kind) throws Exception {
        ensureCommonUnitsRegistered();
        registerBodyCodec(kind);
        BlockingQueue<NetMessage> clientInbound = new LinkedBlockingQueue<>();
        AtomicReference<InboundHandler> clientHandler = new AtomicReference<>();
        NettyNetClientBootstrapSetting clientSetting = clientSetting(kind,
                registerInboundHandlerFactory(false, clientHandler, clientInbound));
        NettyClientGuide clientGuide = new NettyClientGuide(APP_CONTEXT, clientSetting, channelMaker(clientSetting));
        clientGuide.prepareStart();
        return new NetIntegrationHarness(null, clientGuide, 0, new LinkedBlockingQueue<>(),
                clientInbound, new AtomicReference<>(), clientHandler);
    }

    /**
     * 连接任意地址（服务端非本 harness 启动，如 Spring 官方装配场景）。
     */
    public NetTunnel connectTo(InetSocketAddress address) throws ExecutionException, InterruptedException {
        NetTunnel tunnel = clientGuide.connect(URL.valueOf("tcp://" + address.getHostString() + ":" + address.getPort()), t -> {
        });
        clientTunnels.add(tunnel);
        return tunnel;
    }

    public InetSocketAddress serverAddress() {
        return serverAddress;
    }

    public boolean isServerBound() {
        return serverGuide != null && serverGuide.isBound();
    }

    public NetworkContext serverContext() {
        return serverGuide.getContext();
    }

    /**
     * 服务端通道层收到的已解码消息（真实 socket 字节经真实编解码栈产出）。
     */
    public BlockingQueue<NetMessage> serverInbound() {
        return serverInbound;
    }

    /**
     * 客户端通道层收到的已解码消息（含服务端 echo 回执）。
     */
    public BlockingQueue<NetMessage> clientInbound() {
        return clientInbound;
    }

    /**
     * 强制关闭服务端已建立的全部连接（监听器保留）——通道级断开注入点。
     */
    public void disconnectAllServerConnections() {
        InboundHandler handler = serverHandler.get();
        if (handler != null) {
            handler.seenChannels().forEach(channel -> channel.close().awaitUninterruptibly(5000L));
        }
    }

    /**
     * 关闭整个服务端引导器（监听通道 + 子通道组优雅停机，向已建立连接发出 FIN）——
     * specs R3"通信中途对端消失"的进程级注入点，客户端引导器保持存活以供观测。
     */
    public void shutdownServer() {
        if (serverGuide != null) {
            serverGuide.close();
        }
    }

    /**
     * 同一引导器实例重新 open（验证 net-guide-lifecycle"重开合同"：close 释放线程组/构建器后
     * open 须重建并再次受理监听）。须在 {@link #shutdownServer()} 之后调用。
     */
    public void restartServer() {
        serverGuide.open();
    }

    /**
     * 真实客户端隧道建连（阻塞至 open 完成），交由 harness 统一回收。
     */
    public NetTunnel connectClient() throws ExecutionException, InterruptedException {
        NetTunnel tunnel = clientGuide.connect(URL.valueOf("tcp://" + HOST + ":" + serverAddress.getPort()), t -> {
        });
        clientTunnels.add(tunnel);
        return tunnel;
    }

    @Override
    public void close() {
        clientTunnels.forEach(NetTunnel::close);
        clientTunnels.clear();
        clientGuide.close();
        if (serverGuide != null) {
            serverGuide.close();
        }
    }

    /**
     * 用 harness 真实客户端编码器把消息打成线级帧字节（raw socket 注入用，specs R4 兼容锚）：
     * 复用客户端 channel 配置的 encoder 单元名与默认最低特性集（verify/encrypt/waste 全关）。
     * accessId 由固定凭证的载体通道提供，产出字节确定。
     */
    public byte[] encodeClientFrame(NetMessage message) throws Exception {
        NettyBootstrapSetting clientSetting = (NettyBootstrapSetting) clientGuide.getSetting();
        NetPacketCodecSetting encoderSetting = clientSetting.getChannel().getEncoder();
        NetPacketV1Encoder encoder = new NetPacketV1Encoder(encoderSetting);
        encoder.prepareStart();
        EmbeddedChannel carrier = new EmbeddedChannel();
        NetTunnel accessIdCarrier = mock(NetTunnel.class);
        when(accessIdCarrier.getAccessId()).thenReturn(7L);
        carrier.attr(NettyNetAttrKeys.TUNNEL).set(accessIdCarrier);
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(carrier);
        ByteBuf out = Unpooled.buffer();
        try {
            encoder.encodeObject(ctx, message, out);
            byte[] frame = new byte[out.readableBytes()];
            out.readBytes(frame);
            return frame;
        } finally {
            out.release();
            carrier.finishAndReleaseAll();
        }
    }

    private static void ensureCommonUnitsRegistered() {
        if (!COMMON_UNITS_REGISTERED.compareAndSet(false, true)) {
            return;
        }
        ContactAuthenticateService contactAuthenticator = new ContactAuthenticateService(new CommonSessionKeeperManager());
        DefaultMessageDispatcher messageDispatcher = new DefaultMessageDispatcher(APP_CONTEXT, contactAuthenticator);
        registerIfAbsent(MessageFactory.class, MESSAGE_FACTORY, new CommonMessageFactory());
        registerIfAbsent(ContactFactory.class, CONTACT_FACTORY, new DefaultContactFactory());
        registerIfAbsent(SessionFactory.class, SESSION_FACTORY, new CommonSessionFactory());
        registerIfAbsent(CommandExecutorFactory.class, COMMAND_EXECUTOR_FACTORY, new DefaultCommandExecutorFactory());
        registerIfAbsent(RpcMonitor.class, RPC_MONITOR, new RpcMonitor());
        registerIfAbsent(NetIdGenerator.class, ID_GENERATOR, new AutoIncrementIdGenerator());
        registerIfAbsent(NettyTunnelFactory.class, SERVER_TUNNEL_FACTORY, new ServerTunnelFactory());
        registerIfAbsent(MessageHeaderCodec.class, MESSAGE_HEADER_CODEC, new DefaultMessageHeaderCodec());
        registerIfAbsent(CodecVerifier.class, CODEC_VERIFIER, new CRC64CodecVerifier());
        registerIfAbsent(CodecCrypto.class, CODEC_CRYPTO, new XOrCodecCrypto());
        registerIfAbsent(RpcForwarder.class, RPC_FORWARDER, newRpcForwarder());
        // dispatcher 生命周期前置：prepareStart 汇集鉴权/插件/监听单元（harness 场景下均为空集，行为与生产缺省一致）
        messageDispatcher.prepareStart();
        registerIfAbsent(MessageDispatcher.class, MESSAGE_DISPATCHER, messageDispatcher);
    }

    private static void registerBodyCodec(BodyCodecKind kind) {
        registerIfAbsent(MessageBodyCodec.class, kind.unitName(), kind.factory.get());
    }

    /**
     * 单应用 harness 无跨服转发场景：节点管理器以"到达即失败"打桩，保证若误入转发路径会以显式异常暴露。
     */
    private static RpcForwarder newRpcForwarder() {
        RpcForwardNodeManager unavailableNodes = new RpcForwardNodeManager() {
            @Override
            public RpcForwardNodeSet loadForwardNodeSet(RpcServiceType type) {
                throw new IllegalStateException("rpc forwarding is out of harness scope");
            }

            @Override
            public RpcForwardNodeSet findForwardNodeSet(RpcServiceType serviceType) {
                throw new IllegalStateException("rpc forwarding is out of harness scope");
            }
        };
        return new DefaultRpcForwarder(unavailableNodes, new FirstRpcForwarderStrategy(), Collections.emptyList());
    }

    private static <T> void registerIfAbsent(Class<T> unitInterface, String name, T unit) {
        if (UnitLoader.getLoader(unitInterface).getUnit(name).isEmpty()) {
            UnitLoader.register(name, unit);
        }
    }

    /**
     * handler 工厂按 harness 实例注册专属单元名：创建的 InboundHandler 写入 holder，
     * 服务端实例带 REQUEST-echo 行为（specs R3 回执由通道层发出，控制器派发栈不在 harness 范围）。
     */
    private static String registerInboundHandlerFactory(boolean echoRequest, AtomicReference<InboundHandler> holder,
            BlockingQueue<NetMessage> sink) {
        String unitName = "itInboundHandlerFactory-" + HANDLER_UNIT_SEQ.incrementAndGet();
        registerIfAbsent(NettyMessageHandlerFactory.class, unitName,
                (NettyMessageHandlerFactory) context -> {
                    InboundHandler handler = new InboundHandler(context, sink, echoRequest);
                    holder.set(handler);
                    return handler;
                });
        return unitName;
    }

    private static NettyNetServerBootstrapSetting serverSetting(int port, BodyCodecKind kind, String handlerUnit) {
        NettyNetServerBootstrapSetting setting = new NettyNetServerBootstrapSetting(kind.unitName, kind.unitName);
        setting.setName("IT-SERVER-" + kind.name());
        setting.setBindAddress(HOST + ":" + port);
        applyUnitNames(setting, handlerUnit);
        return setting;
    }

    private static NettyNetClientBootstrapSetting clientSetting(BodyCodecKind kind, String handlerUnit) {
        NettyChannelSetting channel = new NettyChannelSetting(kind.unitName, kind.unitName);
        NettyNetClientBootstrapSetting setting = new NettyNetClientBootstrapSetting(channel);
        setting.setName("IT-CLIENT-" + kind.name());
        applyUnitNames(setting, handlerUnit);
        return setting;
    }

    private static void applyUnitNames(CommonNetBootstrapSetting setting, String handlerUnit) {
        setting.setMessageFactory(MESSAGE_FACTORY)
                .setContactFactory(CONTACT_FACTORY)
                .setSessionFactory(SESSION_FACTORY)
                .setMessageDispatcher(MESSAGE_DISPATCHER)
                .setCommandExecutorFactory(COMMAND_EXECUTOR_FACTORY)
                .setRpcForwarder(RPC_FORWARDER)
                .setTunnelIdGenerator(ID_GENERATOR);
        NettyChannelSetting channel = ((NettyBootstrapSetting) setting).getChannel();
        channel.setMessageHandlerFactory(handlerUnit);
        channel.setTunnelFactory(SERVER_TUNNEL_FACTORY);
        // 编解码器内解析的是 encoder/decoder 各自配置里的单元名，须一并对齐 harness 专属前缀
        channel.getEncoder().setMessageHeaderCodec(MESSAGE_HEADER_CODEC).setVerifier(CODEC_VERIFIER).setCrypto(CODEC_CRYPTO);
        channel.getDecoder().setMessageHeaderCodec(MESSAGE_HEADER_CODEC).setVerifier(CODEC_VERIFIER).setCrypto(CODEC_CRYPTO);
    }

    /**
     * 复刻生产 registerChannelMaker 配方：编码器/解码器以 harness 配置构造并手工驱动 prepareStart
     * （其单元解析含 body/header/verifier/crypto），再装入 {@link DefaultDatagramChannelMaker}。
     */
    private static ChannelMaker<Channel> channelMaker(NettyBootstrapSetting setting) {
        NettyChannelSetting channelSetting = setting.getChannel();
        NetPacketV1Encoder encoder = new NetPacketV1Encoder(channelSetting.getEncoder());
        NetPacketV1Decoder decoder = new NetPacketV1Decoder(channelSetting.getDecoder());
        encoder.prepareStart();
        decoder.prepareStart();
        return new DefaultDatagramChannelMaker<Channel>(encoder, decoder);
    }

    /**
     * 通道层录制处理器（继承真实 {@link NettyMessageHandler}，收/发/断开事件流照常走父类）：
     * 入站已解码消息记入队列；服务端实例对 REQUEST 在通道层直接回 RESPOND echo——
     * 控制器派发栈不在 harness 范围（装配策略见类注释与 design D2/D3），echo 后消费该消息避免
     * dispatcher 的"无控制器错误回执"污染客户端观测。
     */
    static final class InboundHandler extends NettyMessageHandler {

        private final BlockingQueue<NetMessage> inbound;

        private final boolean echoRequest;

        private final List<Channel> seenChannels = new CopyOnWriteArrayList<>();

        InboundHandler(NetworkContext context, BlockingQueue<NetMessage> inbound, boolean echoRequest) {
            super(context);
            this.inbound = inbound;
            this.echoRequest = echoRequest;
        }

        List<Channel> seenChannels() {
            return seenChannels;
        }

        @Override
        public void channelActive(ChannelHandlerContext context) throws Exception {
            Channel channel = context.channel();
            if (!seenChannels.contains(channel)) {
                seenChannels.add(channel);
            }
            super.channelActive(context);
        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object message) {
            Channel channel = context.channel();
            if (!seenChannels.contains(channel)) {
                seenChannels.add(channel);
            }
            if (message instanceof NetMessage netMessage) {
                inbound.add(netMessage);
                if (echoRequest && netMessage.getMode() == MessageMode.REQUEST) {
                    NetTunnel tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
                    if (tunnel != null) {
                        tunnel.send(MessageContents.respond(new SimpleProtocol(netMessage.getProtocolId(), netMessage.getLine()),
                                netMessage.getBody(), netMessage.getId()));
                    }
                    return;
                }
            }
            super.channelRead(context, message);
        }
    }

    /**
     * 由请求头重建的回执协议（Protocols 内 DefaultProtocol 为 protected，此处独立最小实现）。
     */
    record SimpleProtocol(int protocolId, int line) implements Protocol {

        @Override
        public int getProtocolId() {
            return protocolId;
        }

        @Override
        public int getLine() {
            return line;
        }
    }

    private static int freePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getByName(HOST))) {
            return socket.getLocalPort();
        }
    }

}
