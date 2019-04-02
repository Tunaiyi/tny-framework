package com.tny.game.suite.net.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.protoex.*;
import com.tny.game.suite.initer.*;
import com.tny.game.suite.launcher.*;
import com.tny.game.suite.net.spring.*;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class NetCommonAutoConfiguration {

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
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public AppContext appContext(ApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        return Binder.get(environment)
                .bind("tny.app", Bindable.ofInstance(new SuiteAppContext()))
                .get();
    }

    @Bean
    @Order(-99999)
    public SuitApplication suitApplication(ApplicationContext applicationContext, AppContext appContext) {
        return new SuitApplication(applicationContext, appContext);
    }

    @Bean
    public EndpointKeeperManager endpointKeeperManager() {
        return new CommonEndpointKeeperManager();
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
    public ProtoExCodec<?> protoExCodec() {
        return new ProtoExCodec<>();
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
