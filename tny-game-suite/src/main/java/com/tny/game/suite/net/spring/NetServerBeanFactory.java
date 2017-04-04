package com.tny.game.suite.net.spring;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.config.Config;
import com.tny.game.common.word.LocalWordsFilter;
import com.tny.game.common.word.WordsFilter;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.MessageDispatcher;
import com.tny.game.net.common.checker.MessageMD5Signer;
import com.tny.game.net.common.checker.MessageSequenceChecker;
import com.tny.game.net.common.checker.MessageTimeoutChecker;
import com.tny.game.net.common.dispatcher.ForkJoinDispatchCommandExecutor;
import com.tny.game.net.common.handle.ForkJoinSessionEventHandler;
import com.tny.game.net.common.session.CommonSessionFactory;
import com.tny.game.net.common.session.CommonSessionHolder;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.message.protoex.ProtoExMessageBuilderFactory;
import com.tny.game.net.netty.NettyServer;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.session.holder.SessionHolder;
import com.tny.game.net.spring.SpringMessageDispatcher;
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
@Profile({SERVER, CLIENT, GAME, SERVER_KAFKA, GAME_KAFKA})
public class NetServerBeanFactory {

    @Resource(name = "gameServerConfiguration")
    private AppConfiguration appConfiguration;

    @Resource(name = "gameServerChannelMaker")
    private ChannelMaker<Channel> channelMaker;

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
        return new ForkJoinDispatchCommandExecutor(this.appConfiguration.getProperties());
    }

    @Bean(name = "messageDispatcher")
    public MessageDispatcher getMessageDispatcher() {
        return new SpringMessageDispatcher(this.appConfiguration);
    }

    @Bean(name = "messageBuilderFactory")
    public MessageBuilderFactory getMessageBuilderFactory() {
        return new ProtoExMessageBuilderFactory<>();
    }

    @Bean(name = "signGenerator")
    public MessageMD5Signer getMessageSignChecker() {
        Config config = Configs.SUITE_CONFIG;
        // String protsWords = config.getStr(Configs.SUITE_MSG_CHECKER_DIRECT_PROTS);
        // List<Integer> ports = new ArrayList<>();
        // if (protsWords != null) {
        //     ports = Arrays.stream(StringUtils.split(protsWords, ","))
        //             .map(NumberUtils::toInt)
        //             .collect(Collectors.toList());
        // }
        String randomWords = config.getStr(Configs.SUITE_MSG_CHECKER_RANDOM_SEQ);
        short[] randomKey = new short[0];
        if (randomWords != null) {
            randomKey = ArrayUtils.toPrimitive(
                    Arrays.stream(StringUtils.split(randomWords, ","))
                            .map(NumberUtils::toShort)
                            .collect(Collectors.toList())
                            .toArray(new Short[0]));
        }
        // return new  GameMD5VerifyChecker(randomKey, ImmutableSet.of(Session.UNLOGIN_USER_GROUP, Session.DEFAULT_USER_GROUP));
        return new GameMessageMD5Signer(randomKey, ImmutableSet.of());
    }

    @Bean(name = "messageSequenceChecker")
    public MessageSequenceChecker messageSequenceChecker() {
        return new MessageSequenceChecker();
    }

    @Bean(name = "messageTimeoutChecker")
    public MessageTimeoutChecker messageTimeoutChecker() {
        return new MessageTimeoutChecker();
    }

    @Bean(name = "messageDispatcher")
    public SpringMessageDispatcher messageDispatcher() {
        return new SpringMessageDispatcher(this.appConfiguration);
    }

    @Bean
    public WordsFilter wordsFilter() {
        return new LocalWordsFilter(
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_FILTER_CONFIG_PATH, Configs.WORD_FILTER_CONFIG_PATH),
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_REPLACE_SYMBOL, "*"));
    }

    @Bean
    @Profile({SERVER, GAME})
    public NettyServer nettyServer() {
        return new NettyServer(this.channelMaker, this.appConfiguration);
    }

}
