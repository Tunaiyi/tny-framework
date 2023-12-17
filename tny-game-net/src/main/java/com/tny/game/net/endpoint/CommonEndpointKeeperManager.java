/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.endpoint;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.event.bus.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.rpc.*;
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

    private static final ContactType DEFAULT_KEY = new ContactType() {

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

    private final Map<ContactType, NetEndpointKeeper<?>> endpointKeeperMap = new ConcurrentHashMap<>();

    private Map<ContactType, SessionKeeperSetting> sessionKeeperSettingMap = ImmutableMap.of();

    private Map<ContactType, TerminalKeeperSetting> terminalKeeperSettingMap = ImmutableMap.of();

    private final Map<String, SessionKeeperFactory<SessionKeeperSetting>> sessionFactoryMap = new ConcurrentHashMap<>();

    private final Map<String, TerminalKeeperFactory<TerminalKeeperSetting>> terminalFactoryMap = new ConcurrentHashMap<>();

    @SuppressWarnings({"rawtypes"})
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
        Map<ContactType, SessionKeeperSetting> sessionSettingMap = new HashMap<>();
        Map<ContactType, TerminalKeeperSetting> terminalSettingMap = new HashMap<>();
        if (MapUtils.isNotEmpty(sessionKeeperSettingMap)) {
            sessionKeeperSettingMap.forEach((name, setting) -> sessionSettingMap
                    .put(ContactTypes.checkGroup(StringAide.ifBlank(setting.getContactType(), name)), setting));
        }
        if (MapUtils.isNotEmpty(terminalKeeperSettingMap)) {
            terminalKeeperSettingMap.forEach((name, setting) -> terminalSettingMap
                    .put(ContactTypes.checkGroup(StringAide.ifBlank(setting.getContactType(), name)), setting));
        }
        if (defaultSessionKeeperSetting != null) {
            sessionSettingMap.put(DEFAULT_KEY, defaultSessionKeeperSetting);
            if (StringUtils.isNoneBlank(defaultSessionKeeperSetting.getContactType())) {
                sessionSettingMap.put(ContactTypes.checkGroup(defaultSessionKeeperSetting.getContactType()), defaultSessionKeeperSetting);
            }
        }
        if (defaultTerminalKeeperSetting != null) {
            terminalSettingMap.put(DEFAULT_KEY, defaultTerminalKeeperSetting);
            if (StringUtils.isNoneBlank(defaultTerminalKeeperSetting.getContactType())) {
                terminalSettingMap.put(ContactTypes.checkGroup(defaultTerminalKeeperSetting.getContactType()), defaultTerminalKeeperSetting);
            }
        }
        this.sessionKeeperSettingMap = ImmutableMap.copyOf(sessionSettingMap);
        this.terminalKeeperSettingMap = ImmutableMap.copyOf(terminalSettingMap);
    }

    private NetEndpointKeeper<?> create(ContactType contactType, NetAccessMode accessMode) {
        if (accessMode == NetAccessMode.SERVER) {
            SessionKeeperSetting setting = this.sessionKeeperSettingMap.get(contactType);
            if (setting == null) {
                setting = this.sessionKeeperSettingMap.get(DEFAULT_KEY);
            }
            return create(contactType, accessMode, setting, this.sessionFactoryMap.get(setting.getKeeperFactory()));
        } else {
            TerminalKeeperSetting setting = this.terminalKeeperSettingMap.get(contactType);
            if (setting == null) {
                setting = this.terminalKeeperSettingMap.get(DEFAULT_KEY);
            }
            return create(contactType, accessMode, setting, this.terminalFactoryMap.get(setting.getKeeperFactory()));
        }
    }

    private <E extends Endpoint, EK extends NetEndpointKeeper<E>, S extends EndpointKeeperSetting> EK create(
            ContactType contactType, NetAccessMode accessMode, S setting, EndpointKeeperFactory<EK, S> factory) {
        Asserts.checkNotNull(factory, "can not found {}.{} session factory", accessMode, contactType);
        return factory.createKeeper(contactType, setting);
    }

    @Override
    public <K extends EndpointKeeper<? extends Endpoint>> K loadEndpoint(ContactType contactType, NetAccessMode accessMode) {
        EndpointKeeper<?> keeper = this.endpointKeeperMap.get(contactType);
        if (keeper != null) {
            return as(keeper);
        }
        NetEndpointKeeper<?> newOne = create(contactType, accessMode);
        keeper = as(this.endpointKeeperMap.computeIfAbsent(contactType, (k) -> newOne));
        if (keeper == newOne) {
            ON_CREATE.notify(keeper);
        }
        return as(keeper);
    }

    @Override
    public <K extends EndpointKeeper<? extends Endpoint>> Optional<K> getKeeper(ContactType userType) {
        return Optional.ofNullable(as(this.endpointKeeperMap.get(userType)));
    }

    @Override
    public <K extends SessionKeeper> Optional<K> getSessionKeeper(ContactType userType) {
        return this.getKeeper(userType, SessionKeeper.class);
    }

    @Override
    public <K extends TerminalKeeper> Optional<K> getTerminalKeeper(ContactType userType) {
        return this.getKeeper(userType, TerminalKeeper.class);
    }

    private <K extends EndpointKeeper<?>, EK extends K> Optional<EK> getKeeper(ContactType userType, Class<K> keeperClass) {
        EndpointKeeper<?> keeper = this.endpointKeeperMap.get(userType);
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
        this.sessionKeeperSettingMap.forEach((name, setting) -> this.sessionFactoryMap.computeIfAbsent(setting.getKeeperFactory(),
                f -> UnitLoader.getLoader(SessionKeeperFactory.class).checkUnit(f)));
        this.terminalKeeperSettingMap.forEach((name, setting) -> this.terminalFactoryMap.computeIfAbsent(setting.getKeeperFactory(),
                f -> UnitLoader.getLoader(TerminalKeeperFactory.class).checkUnit(f)));
    }

    private void notifyEndpointOnline(Endpoint endpoint) {
        if (!endpoint.isAuthenticated()) {
            return;
        }
        NetEndpointKeeper<?> keeper = endpointKeeperMap.get(endpoint.getContactType());
        if (keeper != null) {
            keeper.notifyEndpointOnline(endpoint);
        }
    }

    private void notifyEndpointOffline(Endpoint endpoint) {
        if (!endpoint.isAuthenticated()) {
            return;
        }
        NetEndpointKeeper<?> keeper = endpointKeeperMap.get(endpoint.getContactType());
        if (keeper != null) {
            keeper.notifyEndpointOffline(endpoint);
        }
    }

    private void notifyEndpointClose(Endpoint endpoint) {
        if (!endpoint.isAuthenticated()) {
            return;
        }
        NetEndpointKeeper<?> keeper = endpointKeeperMap.get(endpoint.getContactType());
        if (keeper != null) {
            keeper.notifyEndpointClose(endpoint);
        }
    }

}
