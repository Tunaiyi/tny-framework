package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.UnitLoader;
import com.tny.game.common.utils.Throws;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.lifecycle.LifecycleLevel.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 默认 SessionKeeper 工厂
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 16:35
 */
public class DefaultEndpointKeeperManager implements EndpointKeeperManager, AppPrepareStart {

    private Map<String, EndpointKeeper> endpointKeeperMap = new ConcurrentHashMap<>();

    private static final String DEFAULT_SETTING_KEY = "default";

    private Map<String, SessionKeeperSetting> settingMap = new ConcurrentHashMap<>();

    private ClientKeeperFactory<Object> clientKeeperFactory;

    public DefaultEndpointKeeperManager() {
    }

    private EndpointKeeper<?, ?> create(String userType, EndpointType endpointType) {
        if (endpointType == EndpointType.SESSION) {
            SessionKeeperSetting setting = settingMap.get(userType);
            if (setting == null)
                setting = settingMap.get(DEFAULT_SETTING_KEY);
            Throws.checkNotNull(setting, "can not found {}.{} session setting", endpointType, userType);
            SessionKeeperFactory<?, SessionKeeperSetting> factory = UnitLoader.getLoader(SessionKeeperFactory.class)
                    .getUnitAnCheck(setting.getKeeperFactory());
            return factory.createKeeper(userType, setting);
        } else {
            return clientKeeperFactory.createKeeper(userType, null);
        }
    }

    @Override
    public <K extends EndpointKeeper<?, ?>> K loadOcCreate(String userType, EndpointType endpointType) {
        return as(endpointKeeperMap.computeIfAbsent(userType, (k) -> create(k, endpointType)));
    }

    @Override
    public <K extends EndpointKeeper<?, ?>> Optional<K> getKeeper(String userType) {
        return Optional.ofNullable(as(endpointKeeperMap.get(userType)));
    }

    @Override
    public <K extends SessionKeeper<?>> Optional<K> getSessionKeeper(String userType) {
        return getKeeper(userType, SessionKeeper.class);
    }

    @Override
    public <K extends ClientKeeper<?>> Optional<K> getClientKeeper(String userType) {
        return getKeeper(userType, ClientKeeper.class);
    }

    private <K extends EndpointKeeper<?, ?>, U extends K> Optional<U> getKeeper(String userType, Class<K> keeperClass) {
        EndpointKeeper<?, ?> keeper = endpointKeeperMap.get(userType);
        if (keeper == null)
            return Optional.empty();
        if (keeperClass.isInstance(keeper))
            return Optional.of(as(keeper));
        throw new ClassCastException(format("{} not instance of {}", keeper, ClientKeeper.class));
    }


    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        UnitLoader.getLoader(SessionKeeperSetting.class).getAllUnits().forEach(unit -> {
                    UnitLoader.getLoader(SessionKeeperFactory.class).getUnitAnCheck(unit.getKeeperFactory());
                    this.settingMap.put(unit.getName(), unit);
                });
        for (ClientKeeperFactory<?> factory : UnitLoader.getLoader(ClientKeeperFactory.class).getAllUnits()) {
            clientKeeperFactory = as(factory);
            break;
        }
        Throws.checkNotNull(this.clientKeeperFactory, "clientKeeperFactory is null");
    }

}
