package com.tny.game.net.endpoint;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.event.bus.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.transport.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.lifecycle.LifecycleLevel.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 默认 SessionKeeper 工厂
 * <p>
 */
@Unit
public class CommonEndpointKeeperManager implements EndpointKeeperManager, AppPrepareStart {

    private static final MessagerType DEFAULT_KEY = new MessagerType() {

        @Override
        public String getGroup() {
            return "_#default";
        }

        @Override
        public int id() {
            return -1;
        }

        @Override
        public String name() {
            return "_#DEFAULT";
        }

    };

    private final Map<MessagerType, NetEndpointKeeper<?, ?>> endpointKeeperMap = new ConcurrentHashMap<>();

    private Map<MessagerType, SessionKeeperSetting> sessionKeeperSettingMap = ImmutableMap.of();

    private Map<MessagerType, TerminalKeeperSetting> terminalKeeperSettingMap = ImmutableMap.of();

    private final Map<String, SessionKeeperFactory<?, SessionKeeperSetting>> sessionFactoryMap = new ConcurrentHashMap<>();

    private final Map<String, TerminalKeeperFactory<?, TerminalKeeperSetting>> terminalFactoryMap = new ConcurrentHashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final BindVoidEventBus<EndpointKeeperCreateListener, EndpointKeeper> ON_CREATE =
            EventBuses.of(EndpointKeeperCreateListener.class, EndpointKeeperCreateListener::onCreate);

    public CommonEndpointKeeperManager() {
        EndpointEventBuses buses = EndpointEventBuses.buses();
        buses.onlineEvent().add(this::notifyEndpointOnline);
        buses.offlineEvent().add(this::notifyEndpointOffline);
        buses.closeEvent().add(this::notifyEndpointClose);
    }

    public CommonEndpointKeeperManager(
            SessionKeeperSetting defaultSessionKeeperSetting,
            TerminalKeeperSetting defaultTerminalKeeperSetting,
            Map<String, ? extends SessionKeeperSetting> sessionKeeperSettingMap,
            Map<String, ? extends TerminalKeeperSetting> terminalKeeperSettingMap) {
        this();
        Map<MessagerType, SessionKeeperSetting> sessionSettingMap = new HashMap<>();
        Map<MessagerType, TerminalKeeperSetting> terminalSettingMap = new HashMap<>();
        if (MapUtils.isNotEmpty(sessionKeeperSettingMap)) {
            sessionKeeperSettingMap.forEach((name, setting) -> sessionSettingMap
                    .put(MessagerTypes.checkGroup(StringAide.ifBlank(setting.getMessagerType(), name)), setting));
        }
        if (MapUtils.isNotEmpty(terminalKeeperSettingMap)) {
            terminalKeeperSettingMap.forEach((name, setting) -> terminalSettingMap
                    .put(MessagerTypes.checkGroup(StringAide.ifBlank(setting.getMessagerType(), name)), setting));
        }
        if (defaultSessionKeeperSetting != null) {
            sessionSettingMap.put(DEFAULT_KEY, defaultSessionKeeperSetting);
            if (StringUtils.isNoneBlank(defaultSessionKeeperSetting.getMessagerType())) {
                sessionSettingMap.put(MessagerTypes.checkGroup(defaultSessionKeeperSetting.getMessagerType()), defaultSessionKeeperSetting);
            }
        }
        if (defaultTerminalKeeperSetting != null) {
            terminalSettingMap.put(DEFAULT_KEY, defaultTerminalKeeperSetting);
            if (StringUtils.isNoneBlank(defaultTerminalKeeperSetting.getMessagerType())) {
                terminalSettingMap.put(MessagerTypes.checkGroup(defaultTerminalKeeperSetting.getMessagerType()), defaultTerminalKeeperSetting);
            }
        }
        this.sessionKeeperSettingMap = ImmutableMap.copyOf(sessionSettingMap);
        this.terminalKeeperSettingMap = ImmutableMap.copyOf(terminalSettingMap);
    }

    private NetEndpointKeeper<?, ?> create(MessagerType messagerType, TunnelMode tunnelMode) {
        if (tunnelMode == TunnelMode.SERVER) {
            SessionKeeperSetting setting = this.sessionKeeperSettingMap.get(messagerType);
            if (setting == null) {
                setting = this.sessionKeeperSettingMap.get(DEFAULT_KEY);
            }
            return create(messagerType, tunnelMode, setting, this.sessionFactoryMap.get(setting.getKeeperFactory()));
        } else {
            TerminalKeeperSetting setting = this.terminalKeeperSettingMap.get(messagerType);
            if (setting == null) {
                setting = this.terminalKeeperSettingMap.get(DEFAULT_KEY);
            }
            return create(messagerType, tunnelMode, setting, this.terminalFactoryMap.get(setting.getKeeperFactory()));
        }
    }

    private <E extends Endpoint<?>, EK extends NetEndpointKeeper<?, E>, S extends EndpointKeeperSetting> EK create(
            MessagerType messagerType, TunnelMode endpointType, S setting, EndpointKeeperFactory<?, EK, S> factory) {
        Asserts.checkNotNull(factory, "can not found {}.{} session factory", endpointType, messagerType);
        return factory.createKeeper(messagerType, setting);
    }

    @Override
    public <UID, K extends EndpointKeeper<UID, ? extends Endpoint<UID>>> K loadEndpoint(MessagerType userType, TunnelMode tunnelMode) {
        EndpointKeeper<?, ?> keeper = this.endpointKeeperMap.get(userType.getGroup());
        if (keeper != null) {
            return as(keeper);
        }
        NetEndpointKeeper<?, ?> newOne = create(userType, tunnelMode);
        keeper = as(this.endpointKeeperMap.computeIfAbsent(userType, (k) -> newOne));
        if (keeper == newOne) {
            ON_CREATE.notify(keeper);
        }
        return as(keeper);
    }

    @Override
    public <UID, K extends EndpointKeeper<UID, ? extends Endpoint<UID>>> Optional<K> getKeeper(MessagerType userType) {
        return Optional.ofNullable(as(this.endpointKeeperMap.get(userType)));
    }

    @Override
    public <UID, K extends SessionKeeper<UID>> Optional<K> getSessionKeeper(MessagerType userType) {
        return this.getKeeper(userType, SessionKeeper.class);
    }

    @Override
    public <UID, K extends TerminalKeeper<UID>> Optional<K> getTerminalKeeper(MessagerType userType) {
        return this.getKeeper(userType, TerminalKeeper.class);
    }

    private <K extends EndpointKeeper<?, ?>, EK extends K> Optional<EK> getKeeper(MessagerType userType, Class<K> keeperClass) {
        EndpointKeeper<?, ?> keeper = this.endpointKeeperMap.get(userType);
        if (keeper == null) {
            return Optional.empty();
        }
        if (keeperClass.isInstance(keeper)) {
            return Optional.of(as(keeper));
        }
        throw new ClassCastException(format("{} not instance of {}", keeper, keeperClass));
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        this.sessionKeeperSettingMap.forEach((name, setting) -> {
            this.sessionFactoryMap.computeIfAbsent(setting.getKeeperFactory(), f -> UnitLoader.getLoader(SessionKeeperFactory.class).checkUnit(f));
        });
        this.terminalKeeperSettingMap.forEach((name, setting) -> {
            this.terminalFactoryMap.computeIfAbsent(setting.getKeeperFactory(), f -> UnitLoader.getLoader(TerminalKeeperFactory.class).checkUnit(f));
        });
    }

    private void notifyEndpointOnline(Endpoint<?> endpoint) {
        if (!endpoint.isAuthenticated()) {
            return;
        }
        NetEndpointKeeper<?, ?> keeper = endpointKeeperMap.get(endpoint.getUserGroup());
        if (keeper != null) {
            keeper.notifyEndpointOnline(endpoint);
        }
    }

    private void notifyEndpointOffline(Endpoint<?> endpoint) {
        if (!endpoint.isAuthenticated()) {
            return;
        }
        NetEndpointKeeper<?, ?> keeper = endpointKeeperMap.get(endpoint.getUserGroup());
        if (keeper != null) {
            keeper.notifyEndpointOffline(endpoint);
        }
    }

    private void notifyEndpointClose(Endpoint<?> endpoint) {
        if (!endpoint.isAuthenticated()) {
            return;
        }
        NetEndpointKeeper<?, ?> keeper = endpointKeeperMap.get(endpoint.getUserGroup());
        if (keeper != null) {
            keeper.notifyEndpointClose(endpoint);
        }
    }

}
