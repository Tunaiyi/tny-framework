package com.tny.game.suite.net.configuration;

import com.tny.game.net.base.AppContext;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.CRC64CodecVerifier;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.ParamFilterPlugin;
import com.tny.game.net.message.MessageFactory;
import com.tny.game.net.message.protoex.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.suite.initer.*;
import com.tny.game.suite.net.spring.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.annotation.Resource;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class NetCommonAutoConfiguration {

    @Resource
    private EndpointKeeperManager endpointKeeperManager;

    @Bean
    public ProtoExSchemaIniter protoExSchemaIniter() {
        return new ProtoExSchemaIniter();
    }

    @Bean
    public UnitLoadIniter unitLoadIniter() {
        return new UnitLoadIniter();
    }

    @Bean
    public EventDispatcherIniter eventDispatcherIniter() {
        return new EventDispatcherIniter();
    }

    @Bean
    @ConfigurationProperties("tny.app")
    public AppContext appContext() {
        return new SuiteAppContext();
    }

    // @Bean
    // public SuitApplication application() {
    //     return new SuitApplication(applicationContext);
    // }

    @Bean
    public MessageFactory protoExMessageFactory() {
        return new ProtoExMessageFactory<>();
    }

    @Bean
    public MessageDispatcher messageDispatcher(@Qualifier("appContext") AppContext appContext) {
        return new SuiteMessageDispatcher(appContext);
    }

    @Bean
    public ClientKeeperFactory clientKeeperFactory() {
        return new DefaultClientKeeperFactory();
    }

    @Bean
    public EndpointKeeperManager endpointKeeperManager() {
        return new DefaultEndpointKeeperManager();
    }

    @Bean
    public MessageSequenceCheckerPlugin messageSequenceCheckerPlugin() {
        return new MessageSequenceCheckerPlugin();
    }

    @Bean
    public ParamFilterPlugin paramFilterPlugin() {
        return new SuiteParamFilterPlugin();
    }

    @Bean
    public BindSessionPlugin bindSessionPlugin() {
        return new BindSessionPlugin(endpointKeeperManager);
    }

    @Bean
    public ProtoExCodec<?> protoExCodec(MessageFactory<Object> messageFactory) {
        return new ProtoExCodec<>(messageFactory);
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
