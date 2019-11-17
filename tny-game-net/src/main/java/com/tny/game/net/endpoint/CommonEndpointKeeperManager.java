package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.lifecycle.LifecycleLevel.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 默认 SessionKeeper 工厂
 * <p>
 */
public class CommonEndpointKeeperManager implements EndpointKeeperManager, AppPrepareStart {

    private static final String DEFAULT_KEY = "default";

    private Map<String, EndpointKeeper> endpointKeeperMap = new ConcurrentHashMap<>();

    private Map<String, SessionSetting> sessionSettingMap = new ConcurrentHashMap<>();

    private Map<String, TerminalSetting> terminalSettingMap = new ConcurrentHashMap<>();

    private Map<String, SessionKeeperFactory<?, SessionSetting>> sessionFactoryMap = new ConcurrentHashMap<>();

    private Map<String, TerminalKeeperFactory<?, TerminalSetting>> terminalFactoryMap = new ConcurrentHashMap<>();

    public CommonEndpointKeeperManager() {
    }

    private EndpointKeeper<?, ?> create(String userType, TunnelMode tunnelMode) {
        if (tunnelMode == TunnelMode.SERVER) {
            SessionSetting setting = this.sessionSettingMap.get(userType);
            if (setting == null)
                setting = this.sessionSettingMap.get(DEFAULT_KEY);
            return create(userType, tunnelMode, setting, this.sessionFactoryMap.get(setting.getKeeperFactory()));
        } else {
            TerminalSetting setting = this.terminalSettingMap.get(userType);
            if (setting == null)
                setting = this.terminalSettingMap.get(DEFAULT_KEY);
            return create(userType, tunnelMode, setting, this.terminalFactoryMap.get(setting.getKeeperFactory()));
        }
    }

    private <E extends Endpoint<?>, EK extends EndpointKeeper<?, E>, S extends EndpointSetting> EK create(
            String userType, TunnelMode endpointType, S setting, EndpointKeeperFactory<?, EK, S> factory) {
        Throws.checkNotNull(factory, "can not found {}.{} session factory", endpointType, userType);
        return factory.createKeeper(userType, setting);
    }

    @Override
    public <UID, K extends EndpointKeeper<UID, ? extends Endpoint<UID>>> K loadOrCreate(String userType, TunnelMode tunnelMode) {
        return as(this.endpointKeeperMap.computeIfAbsent(userType, (k) -> create(k, tunnelMode)));
    }

    @Override
    public <UID, K extends EndpointKeeper<UID, ? extends Endpoint<UID>>> Optional<K> getKeeper(String userType) {
        return Optional.ofNullable(as(this.endpointKeeperMap.get(userType)));
    }

    @Override
    public <UID, K extends SessionKeeper<UID>> Optional<K> getSessionKeeper(String userType) {
        return this.getKeeper(userType, SessionKeeper.class);
    }

    @Override
    public <UID, K extends TerminalKeeper<UID>> Optional<K> getClientKeeper(String userType) {
        return this.getKeeper(userType, TerminalKeeper.class);
    }


    private <K extends EndpointKeeper<?, ?>, EK extends K> Optional<EK> getKeeper(String userType, Class<K> keeperClass) {
        EndpointKeeper<?, ?> keeper = this.endpointKeeperMap.get(userType);
        if (keeper == null)
            return Optional.empty();
        if (keeperClass.isInstance(keeper))
            return Optional.of(as(keeper));
        throw new ClassCastException(format("{} not instance of {}", keeper, keeperClass));
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        UnitLoader.getLoader(SessionSetting.class).getAllUnits().forEach(unit -> {
            this.sessionSettingMap.put(unit.getName(), as(unit));
            this.sessionFactoryMap.computeIfAbsent(unit.getKeeperFactory(), f -> UnitLoader.getLoader(SessionKeeperFactory.class).getUnitAnCheck(f));
        });
        UnitLoader.getLoader(TerminalSetting.class).getAllUnits().forEach(unit -> {
            this.terminalSettingMap.put(unit.getName(), as(unit));
            this.terminalFactoryMap
                    .computeIfAbsent(unit.getKeeperFactory(), f -> UnitLoader.getLoader(TerminalKeeperFactory.class).getUnitAnCheck(f));
        });
    }

}
