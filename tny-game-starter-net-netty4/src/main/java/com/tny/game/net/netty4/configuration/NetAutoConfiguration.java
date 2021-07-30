package com.tny.game.net.netty4.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.codec.protoex.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.appliaction.*;
import com.tny.game.net.netty4.configuration.app.*;
import com.tny.game.net.netty4.configuration.endpoint.*;
import com.tny.game.net.netty4.configuration.guide.*;
import com.tny.game.net.netty4.configuration.processor.disruptor.*;
import com.tny.game.net.netty4.configuration.processor.forkjoin.*;
import com.tny.game.net.netty4.spring.*;
import com.tny.game.net.transport.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties({
        SpringBootNetAppProperties.class,
        SpringNetEndpointProperties.class,
        SpringBootNetBootstrapProperties.class,
        DisruptorEndpointCommandTaskProcessorProperties.class,
        ForkJoinEndpointCommandTaskProcessorProperties.class,
})
public class NetAutoConfiguration {

    @Bean
    public CertificateFactory<?> defaultCertificateFactory() {
        return new DefaultCertificateFactory<>();
    }

    @Bean
    @ConditionalOnMissingBean(EndpointKeeperManager.class)
    public EndpointKeeperManager endpointKeeperManager(SpringNetEndpointProperties configure) {
        return new CommonEndpointKeeperManager(
                configure.getSessionKeeper(),
                configure.getTerminalKeeper(),
                configure.getSessionKeeperSettings(),
                configure.getTerminalKeeperSettings());
    }

    @Bean
    @ConditionalOnMissingBean(EndpointService.class)
    @ConditionalOnBean(EndpointKeeperManager.class)
    public EndpointService endpointService() {
        return new EndpointService();
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
    public NetAppContext appContext(SpringBootNetAppProperties configure) {
        return new SpringBootNetAppContext(configure);
    }

    @Bean
    @ConditionalOnBean({EndpointKeeperManager.class, NetAppContext.class})
    public MessageDispatcher defaultMessageDispatcher(NetAppContext appContext, EndpointKeeperManager endpointKeeperManager) {
        return new SpringBootMessageDispatcher(appContext, endpointKeeperManager);
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
    @ConditionalOnMissingBean(ProtoExMessageBodyCodec.class)
    public ProtoExMessageBodyCodec<?> protoExCodec() {
        return new ProtoExMessageBodyCodec<>();
    }

    @Bean
    @ConditionalOnMissingBean(TypeProtobufMessageBodyCodec.class)
    public MessageBodyCodec<Object> typeProtobufMessageBodyCodec() {
        return new TypeProtobufMessageBodyCodec<>();
    }

    @Bean
    public CRC64CodecVerifier crc64CodecVerifier() {
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

}
