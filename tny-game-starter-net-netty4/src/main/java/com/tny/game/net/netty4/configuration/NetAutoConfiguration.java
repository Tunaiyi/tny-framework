package com.tny.game.net.netty4.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.base.configuration.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.protoex.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.appliaction.*;
import com.tny.game.net.netty4.configuration.app.*;
import com.tny.game.net.netty4.configuration.endpoint.*;
import com.tny.game.net.netty4.spring.*;
import com.tny.game.net.transport.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableAutoConfiguration
public class NetAutoConfiguration {

    @Bean
    public NetAppContext appContext(SpringBootNetAppConfigure configure) {
        return new DefaultNetAppContext()
                .setName(configure.getName())
                .setAppType(configure.getAppType())
                .setScopeType(configure.getScopeType())
                .setScanPackages(configure.getBasePackages());
    }

    @Bean
    public CertificateFactory<?> defaultCertificateFactory() {
        return new DefaultCertificateFactory<>();
    }

    @Bean
    @ConditionalOnMissingBean(EndpointKeeperManager.class)
    public EndpointKeeperManager endpointKeeperManager(
            SpringNetEndpointConfigure configure) {
        return new CommonEndpointKeeperManager(
                configure.getSessionKeeper(),
                configure.getTerminalKeeper(),
                configure.getSessionKeeperSettings(),
                configure.getTerminalKeeperSettings());
    }

    @Bean
    @ConditionalOnMissingBean(EndpointService.class)
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
    public MessageDispatcher defaultMessageDispatcher(NetAppContext appContext, EndpointKeeperManager endpointKeeperManager) {
        return new SpringBootMessageDispatcher(appContext, endpointKeeperManager);
    }

    @Bean
    //    @Order(-99999)
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
    public ProtoExMessageBodyCodec<?> protoExCodec() {
        return new ProtoExMessageBodyCodec<>();
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

    // @Bean(name = "messageTimeoutChecker")
    // public MessageTimeoutCheckerPlugin messageTimeoutChecker() {
    //     return new MessageTimeoutCheckerPlugin();
    // }

}
