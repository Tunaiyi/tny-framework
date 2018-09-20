package com.tny.game.net.transport;

import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;

import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.as;

/**
 * 默认 SessionKeeper 工厂
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 16:35
 */
@Unit("CommonSessionKeeperFactory")
public class CommonSessionKeeperFactory implements SessionKeeperFactory {

    private static final ConcurrentMap<String, NetSessionKeeper<?>> KEEPER_MAP = new ConcurrentHashMap<>();

    private AppConfiguration appConfiguration;

    public CommonSessionKeeperFactory(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public <UID> NetSessionKeeper<UID> getKeeper(String userType) {
        return as(KEEPER_MAP.computeIfAbsent(userType, this::create));
    }

    private <UID> NetSessionKeeper<UID> create(String userType) {
        return new CommonSessionKeeper<>(userType,
                appConfiguration.getSessionFactory(), appConfiguration.getProperties());
    }

}
