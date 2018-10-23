package com.tny.game.net.session;

import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.exception.SessionException;

import java.util.Map;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 默认 SessionKeeper 工厂
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 16:35
 */
@Unit("CommonSessionKeeperFactory")
public class CommonSessionKeeperMannager implements SessionKeeperMannager {

    private static final ConcurrentMap<String, SessionKeeper<?>> KEEPER_MAP = new ConcurrentHashMap<>();

    private SessionKeeperConfigurer<?> defaultFactory;

    private Map<String, SessionKeeperConfigurer<?>> configurers = new CopyOnWriteMap<>();

    @Override
    public <UID> SessionKeeper<UID> getKeeper(String userType) {
        return as(KEEPER_MAP.computeIfAbsent(userType, this::create));
    }

    private <UID> SessionKeeper<UID> create(String userType) {
        SessionKeeperConfigurer<?> configurer = configurers.getOrDefault(userType, defaultFactory);
        if (configurer == null)
            throw new SessionException(format("userType {} on exist configurer", userType));
        return new CommonSessionKeeper<>(userType, as(configurer));
    }

    public CommonSessionKeeperMannager setDefaultFactory(SessionKeeperConfigurer<?> defaultFactory) {
        this.defaultFactory = defaultFactory;
        return this;
    }

    public CommonSessionKeeperMannager addSessionFactory(String userType, SessionKeeperConfigurer<?> configurer) {
        this.configurers.put(userType, configurer);
        return this;
    }

    public CommonSessionKeeperMannager addSessionFactories(Map<String, SessionKeeperConfigurer<?>> configurers) {
        this.configurers.putAll(configurers);
        return this;
    }
}
