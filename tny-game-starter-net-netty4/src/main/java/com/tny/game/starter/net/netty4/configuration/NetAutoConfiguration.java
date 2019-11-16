package com.tny.game.starter.net.netty4.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.protoex.*;
import com.tny.game.starter.net.netty4.*;
import com.tny.game.starter.net.netty4.appliaction.*;
import com.tny.game.starter.net.netty4.spring.*;
import org.springframework.boot.context.properties.bind.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class NetAutoConfiguration {

    @Bean
    public com.tny.game.starter.common.Initiator.UnitLoadInitiator unitLoadInitiator() {
        return new com.tny.game.starter.common.Initiator.UnitLoadInitiator();
    }

    @Bean
    public EndpointService endpointService() {
        return new EndpointService();
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
    public NetApplication netApplication(ApplicationContext applicationContext, AppContext appContext) {
        return new NetApplication(applicationContext, appContext);
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
