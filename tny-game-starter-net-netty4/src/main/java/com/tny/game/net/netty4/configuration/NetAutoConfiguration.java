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
package com.tny.game.net.netty4.configuration;

import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import com.tny.game.net.application.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.monitor.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.configuration.channel.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.netty4.configuration.filter.*;
import com.tny.game.net.netty4.configuration.processor.forkjoin.*;
import com.tny.game.net.netty4.configuration.session.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.netty4.network.configuration.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        SpringNetAppProperties.class,
        SpringNetSessionProperties.class,
        ReadIdlePipelineChainProperties.class,
        SerialCommandExecutorProperties.class,})
@Import({TextFilterAutoConfiguration.class})
public class NetAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(SessionKeeperManager.class)
    public SessionKeeperManager sessionKeeperManager(SpringNetSessionProperties configure) {
        return new CommonSessionKeeperManager(configure.getSessionKeeper(),  configure.getSessionKeeperSettings());
    }

    @Bean
    @ConditionalOnMissingBean(ContactService.class)
    @ConditionalOnBean(SessionKeeperManager.class)
    public ContactService contactService(SessionKeeperManager sessionKeeperManager) {
        return new ContactService(sessionKeeperManager);
    }

    @Bean
    public CommandExecutorFactory defaultCommandExecutorFactory() {
        return new DefaultCommandExecutorFactory();
    }

    @Bean
    public NettyMessageHandlerFactory defaultMessageHandlerFactory() {
        return new DefaultMessageHandlerFactory();
    }

    @Bean
    public MessageFactory defaultMessageFactory() {
        return new CommonMessageFactory();
    }

    @Bean
    @ConditionalOnMissingBean(ContactFactory.class)
    public ContactFactory defaultContactFactory() {
        return new DefaultContactFactory();
    }

    @Bean
    public SessionFactory defaultSessionFactory() {
        return new CommonSessionFactory();
    }

    @Bean
    public SessionKeeperFactory<?> defaultSessionKeeperFactory() {
        return new CommonSessionKeeperFactory();
    }

    @Bean
    public NetAppContext appContext(SpringNetAppProperties configure) {
        return new SpringNetAppContext(configure);
    }

    @Bean
    @ConditionalOnMissingBean(ExprHolderFactory.class)
    public ExprHolderFactory exprHolderFactory() {
        return new GroovyExprHolderFactory();
    }

    @Bean
    @ConditionalOnBean({SessionKeeperManager.class})
    public ContactAuthenticateService authenticationService(SessionKeeperManager sessionKeeperManager) {
        return new ContactAuthenticateService(sessionKeeperManager);
    }

    @Bean
    @ConditionalOnBean({SessionKeeperManager.class, ContactAuthenticator.class, NetAppContext.class})
    public MessageDispatcher defaultMessageDispatcher(NetAppContext appContext, ContactAuthenticator contactAuthenticator,
            ExprHolderFactory exprHolderFactory) {
        return new SpringBootMessageDispatcher(appContext, contactAuthenticator, exprHolderFactory);
    }

    @Bean
    public NetIdGenerator defaultNetIdGenerator() {
        return new AutoIncrementIdGenerator();
    }

    @Bean
    public NetApplication netApplication(ApplicationContext applicationContext, NetAppContext appContext) {
        return new NetApplication(applicationContext, appContext);
    }

    @Bean
    public MessageSequenceCheckerPlugin messageSequenceCheckerPlugin() {
        return new MessageSequenceCheckerPlugin();
    }

    @Bean
    public ParamFilterPlugin paramFilterPlugin() {
        return new SpringBootParamFilterPlugin();
    }

    @Bean
    @ConditionalOnClass(ProtoExMessageBodyCodec.class)
    public MessageBodyCodec<?> protoExMessageBodyCodec() {
        return new ProtoExMessageBodyCodec<>();
    }

    @Bean
    @ConditionalOnClass(TypeProtobufMessageBodyCodec.class)
    public MessageBodyCodec<Object> typeProtobufMessageBodyCodec() {
        return new TypeProtobufMessageBodyCodec<>();
    }

    @Bean
    public ControllerRelayStrategy controllerRelayStrategy(MessageDispatcher dispatcher) {
        return new ControllerRelayStrategy(dispatcher);
    }

    @Bean
    public AllRelayStrategy allRelayStrategy() {
        return new AllRelayStrategy();
    }

    @Bean
    public CRC64CodecVerifier cRC64CodecVerifier() {
        return new CRC64CodecVerifier();
    }

    @Bean
    public XOrCodecCrypto xOrCodecCrypto() {
        return new XOrCodecCrypto();
    }

    @Bean
    public NoneCodecCrypto noneCodecCrypto() {
        return new NoneCodecCrypto();
    }

    @Bean
    public ServerTunnelFactory defaultNettyTunnelFactory() {
        return new ServerTunnelFactory();
    }

    @Bean
    public MessageHeaderCodec defaultMessageHeaderCodec() {
        return new DefaultMessageHeaderCodec();
    }

    @Bean
    public NetApplicationLifecycle netApplicationLifecycle() {
        return new NetApplicationLifecycle();
    }

    @Bean
    public RpcForwardStrategy firstRpcForwarderStrategy() {
        return new FirstRpcForwarderStrategy();
    }

    @Bean
    public RpcMonitor rpcMonitor(
            List<RpcMonitorReceiveHandler> receiveHandlers,
            List<RpcMonitorSendHandler> sendHandlers,
            List<RpcMonitorTransferHandler> relayHandlers,
            List<RpcMonitorResumeExecuteHandler> resumeExecuteHandlers,
            List<RpcMonitorSuspendExecuteHandler> suspendExecuteHandlers,
            List<RpcMonitorBeforeInvokeHandler> beforeInvokeHandlers,
            List<RpcMonitorAfterInvokeHandler> afterInvokeHandlers) {
        return new RpcMonitor(receiveHandlers, sendHandlers, relayHandlers,
                resumeExecuteHandlers, suspendExecuteHandlers,
                beforeInvokeHandlers, afterInvokeHandlers);
    }

    @Bean
    public ReadIdlePipelineChain<?> readIdlePipelineChain(ReadIdlePipelineChainProperties properties) {
        return new ReadIdlePipelineChain<>().setIdleTimeout(properties.getIdleTimeout());
    }

}
