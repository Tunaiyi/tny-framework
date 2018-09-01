package com.tny.game.suite.net.spring;

import com.tny.game.common.config.Config;
import com.tny.game.net.command.checker.*;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.common.session.handle.ForkJoinSessionEventHandler;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.message.protoex.ProtoExMessageBuilderFactory;
import com.tny.game.net.netty.NettyAppConfiguration;
import com.tny.game.net.netty.coder.ChannelMaker;
import com.tny.game.net.session.*;
import com.tny.game.net.session.SessionHolder;
import com.tny.game.suite.utils.Configs;
import io.netty.channel.Channel;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
public class NetBaseBeanFactory {

    @Resource(name = "gameServerConfiguration")
    protected NettyAppConfiguration appConfiguration;

    @Bean(name = "sessionHolder")
    public SessionHolder gameSessionHolder() {
        return new CommonSessionHolder(this.appConfiguration.getProperties());
    }

    @Bean(name = "sessionFactory")
    public SessionFactory gameSessionFactory() {
        return new SingleTunnelSessionFactory<Object>(0L, this.appConfiguration);
    }

    @Bean(name = "sessionEventHandler")
    public ForkJoinSessionEventHandler getSessionEventHandler() {
        return new ForkJoinSessionEventHandler(this.appConfiguration);
    }

    @Bean(name = "messageDispatcher")
    public MessageDispatcher getMessageDispatcher() {
        return new SuiteMessageDispatcher(this.appConfiguration);
    }

    @Bean(name = "messageBuilderFactory")
    public MessageBuilderFactory getMessageBuilderFactory() {
        return new ProtoExMessageBuilderFactory<>();
    }

    @Bean(name = "messageSequenceChecker")
    public MessageSequenceChecker messageSequenceChecker() {
        return new MessageSequenceChecker();
    }

    @Bean(name = "messageTimeoutChecker")
    public MessageTimeoutChecker messageTimeoutChecker() {
        return new MessageTimeoutChecker();
    }

    @Bean(name = "channelMaker")
    public ChannelMaker<Channel> channelMaker() {
        return new ReadTimeoutChannelMaker<>();
    }

    private short[] md5RandomKey(Config config) {
        String randomWords = config.getStr(Configs.SUITE_MSG_CHECKER_RANDOM_SEQ);
        short[] randomKey = new short[0];
        if (randomWords != null) {
            randomKey = ArrayUtils.toPrimitive(
                    Arrays.stream(StringUtils.split(randomWords, ","))
                            .map(NumberUtils::toShort)
                            .collect(Collectors.toList())
                            .toArray(new Short[0]));
        }
        return randomKey;
    }


}
