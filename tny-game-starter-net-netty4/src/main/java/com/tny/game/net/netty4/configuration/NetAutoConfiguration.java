package com.tny.game.net.netty4.configuration;

import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import com.tny.game.net.base.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.configuration.channel.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.netty4.configuration.controller.*;
import com.tny.game.net.netty4.configuration.endpoint.*;
import com.tny.game.net.netty4.configuration.filter.*;
import com.tny.game.net.netty4.configuration.processor.disruptor.*;
import com.tny.game.net.netty4.configuration.processor.forkjoin.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.netty4.network.configuration.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SpringNetAppProperties.class, SpringNetEndpointProperties.class, ReadIdlePipelineChainProperties.class,
        DisruptorEndpointCommandTaskProcessorProperties.class, ForkJoinEndpointCommandTaskProcessorProperties.class,})
@Import({TextFilterAutoConfiguration.class, ImportControllerBeanDefinitionRegistrar.class,})
public class NetAutoConfiguration {

    @Bean
    public CertificateFactory<?> defaultCertificateFactory() {
        return new DefaultCertificateFactory<>();
    }

    @Bean
    @ConditionalOnMissingBean(EndpointKeeperManager.class)
    public EndpointKeeperManager endpointKeeperManager(SpringNetEndpointProperties configure) {
        return new CommonEndpointKeeperManager(configure.getSessionKeeper(), configure.getTerminalKeeper(), configure.getSessionKeeperSettings(),
                configure.getTerminalKeeperSettings());
    }

    @Bean
    @ConditionalOnMissingBean(EndpointService.class)
    @ConditionalOnBean(EndpointKeeperManager.class)
    public EndpointService endpointService() {
        return new EndpointService();
    }

    @Bean
    public NettyMessageHandler defaultNettyMessageHandler() {
        return new NettyMessageHandler();
    }

    @Bean
    public MessageFactory defaultMessageFactory() {
        return new CommonMessageFactory();
    }

    @Bean
    public SessionFactory<?, ?, ?> defaultSessionFactory() {
        return new CommonSessionFactory<>();
    }

    @Bean
    public SessionKeeperFactory<?, ?> defaultSessionKeeperFactory() {
        return new CommonSessionKeeperFactory<>();
    }

    @Bean
    public TerminalKeeperFactory<?, ?> defaultTerminalKeeperFactory() {
        return new CommonTerminalKeeperFactory<>();
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
    @ConditionalOnBean({EndpointKeeperManager.class, NetAppContext.class})
    public MessageDispatcher defaultMessageDispatcher(NetAppContext appContext, EndpointKeeperManager endpointKeeperManager,
            ExprHolderFactory exprHolderFactory) {
        return new SpringBootMessageDispatcher(appContext, endpointKeeperManager, exprHolderFactory);
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
    public ParamFilterPlugin<?> paramFilterPlugin() {
        return new SpringBootParamFilterPlugin<>();
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
    public NetApplicationLifecycle netApplicationLifecycle() {
        return new NetApplicationLifecycle();
    }

    @Bean
    public ReadIdlePipelineChain<?> readIdlePipelineChain(ReadIdlePipelineChainProperties properties) {
        return new ReadIdlePipelineChain<>().setIdleTimeout(properties.getIdleTimeout());
    }

}
