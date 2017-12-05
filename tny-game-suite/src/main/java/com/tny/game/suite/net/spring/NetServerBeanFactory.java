package com.tny.game.suite.net.spring;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.config.Config;
import com.tny.game.common.word.LocalWordsFilter;
import com.tny.game.common.word.WordsFilter;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.checker.MessageMD5Checker;
import com.tny.game.net.command.checker.MessageSequenceChecker;
import com.tny.game.net.command.checker.MessageTimeoutChecker;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.command.executor.GroupBySessionDispatchCommandExecutor;
import com.tny.game.net.common.session.handle.ForkJoinSessionEventHandler;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.message.protoex.ProtoExMessageBuilderFactory;
import com.tny.game.net.message.sign.MessageMD5Signer;
import com.tny.game.net.netty.NettyAppConfiguration;
import com.tny.game.net.netty.NettyServer;
import com.tny.game.net.netty.coder.ChannelMaker;
import com.tny.game.net.session.CommonSessionFactory;
import com.tny.game.net.session.CommonSessionHolder;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.session.holder.SessionHolder;
import com.tny.game.suite.login.GameMessageMD5Checker;
import com.tny.game.suite.login.GameMessageMD5Signer;
import com.tny.game.suite.utils.Configs;
import io.netty.channel.Channel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@Profile({GAME})
public class NetServerBeanFactory {

    @Resource(name = "gameServerConfiguration")
    private NettyAppConfiguration appConfiguration;

    @Bean(name = "sessionHolder")
    public SessionHolder gameSessionHolder() {
        return new CommonSessionHolder(this.appConfiguration.getProperties());
    }

    @Bean(name = "sessionFactory")
    public SessionFactory gameSessionFactory() {
        return new CommonSessionFactory<Object>(0L, this.appConfiguration);
    }

    @Bean(name = "sessionEventHandler")
    public ForkJoinSessionEventHandler getSessionEventHandler() {
        return new ForkJoinSessionEventHandler(this.appConfiguration);
    }

    @Bean(name = "dispatchCommandExecutor")
    public DispatchCommandExecutor getDispatchCommandExecutor() {
        return new GroupBySessionDispatchCommandExecutor(this.appConfiguration.getProperties());
    }

    @Bean(name = "messageDispatcher")
    public MessageDispatcher getMessageDispatcher() {
        return new SuiteMessageDispatcher(this.appConfiguration);
    }

    @Bean(name = "messageBuilderFactory")
    public MessageBuilderFactory getMessageBuilderFactory() {
        return new ProtoExMessageBuilderFactory<>();
    }

    @Bean(name = "signGenerator")
    public MessageMD5Signer getMessageSignSigner() {
        Config config = Configs.SUITE_CONFIG;
        short[] randomKey = md5RandomKey(config);
        return new GameMessageMD5Signer(randomKey);
    }

    @Bean(name = "messageChecker")
    public MessageMD5Checker getMessageSignChecker() {
        Config config = Configs.SUITE_CONFIG;
        short[] randomKey = md5RandomKey(config);
        String signGroupsWords = config.getStr(Configs.SUITE_MSG_SIGNER_SIGN_GROUPS, "");
        return new GameMessageMD5Checker(randomKey, ImmutableSet.copyOf(StringUtils.split(signGroupsWords, ",")));
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

    @Bean
    public WordsFilter wordsFilter() throws Exception {
        LocalWordsFilter filter = new LocalWordsFilter(
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_FILTER_CONFIG_PATH, Configs.WORD_FILTER_CONFIG_PATH),
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_REPLACE_SYMBOL, "*"));
        filter.load();
        return filter;
    }

    @Bean
    @Profile({SERVER, GAME})
    public NettyServer nettyServer() {
        return new NettyServer(this.appConfiguration);
    }

}
