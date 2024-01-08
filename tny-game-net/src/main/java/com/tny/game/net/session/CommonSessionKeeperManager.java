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
package com.tny.game.net.session;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.event.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.listener.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.lifecycle.LifecycleLevel.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 默认 SessionKeeper 工厂
 * <p>
 */
@Unit
public class CommonSessionKeeperManager implements SessionKeeperManager, AppPrepareStart {

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

    private final Map<ContactType, NetSessionKeeper> sessionKeeperMap = new ConcurrentHashMap<>();

    private Map<ContactType, SessionKeeperSetting> sessionKeeperSettingMap = ImmutableMap.of();

    private final Map<String, SessionKeeperFactory<SessionKeeperSetting>> sessionFactoryMap = new ConcurrentHashMap<>();

    private static final VoidBindEvent<SessionKeeperCreateListener, SessionKeeper> ON_CREATE =
            Events.ofEvent(SessionKeeperCreateListener.class, SessionKeeperCreateListener::onCreate);

    public CommonSessionKeeperManager() {
        SessionEvents.globalOnlineWatch().addListener(this::notifyOnline);
        SessionEvents.globalOfflineWatch().addListener(this::notifyOffline);
        SessionEvents.globalCloseWatch().addListener(this::notifyClose);
    }

    public CommonSessionKeeperManager(
            SessionKeeperSetting defaultSessionKeeperSetting,
            Map<String, ? extends SessionKeeperSetting> sessionKeeperSettingMap) {
        this();
        Map<ContactType, SessionKeeperSetting> sessionSettingMap = new HashMap<>();
        if (MapUtils.isNotEmpty(sessionKeeperSettingMap)) {
            sessionKeeperSettingMap.forEach((name, setting) -> sessionSettingMap
                    .put(ContactTypes.checkGroup(StringAide.ifBlank(setting.getContactType(), name)), setting));
        }
        if (defaultSessionKeeperSetting != null) {
            sessionSettingMap.put(DEFAULT_KEY, defaultSessionKeeperSetting);
            if (StringUtils.isNoneBlank(defaultSessionKeeperSetting.getContactType())) {
                sessionSettingMap.put(ContactTypes.checkGroup(defaultSessionKeeperSetting.getContactType()), defaultSessionKeeperSetting);
            }
        }
        this.sessionKeeperSettingMap = ImmutableMap.copyOf(sessionSettingMap);
    }

    private NetSessionKeeper create(ContactType contactType, NetAccessMode accessMode) {
        SessionKeeperSetting setting = this.sessionKeeperSettingMap.get(contactType);
        if (setting == null) {
            setting = this.sessionKeeperSettingMap.get(DEFAULT_KEY);
        }
        return create(contactType, accessMode, setting, this.sessionFactoryMap.get(setting.getKeeperFactory()));
    }

    private NetSessionKeeper create(ContactType contactType, NetAccessMode accessMode, SessionKeeperSetting setting,
            SessionKeeperFactory<SessionKeeperSetting> factory) {
        Asserts.checkNotNull(factory, "can not found {}.{} session factory", accessMode, contactType);
        return factory.createKeeper(contactType, setting);
    }

    @Override
    public SessionKeeper loadKeeper(ContactType contactType, NetAccessMode accessMode) {
        SessionKeeper keeper = this.sessionKeeperMap.get(contactType);
        if (keeper != null) {
            return as(keeper);
        }
        NetSessionKeeper newOne = create(contactType, accessMode);
        keeper = as(this.sessionKeeperMap.computeIfAbsent(contactType, (k) -> newOne));
        if (keeper == newOne) {
            ON_CREATE.notify(keeper);
        }
        return as(keeper);
    }

    @Override
    public Optional<SessionKeeper> getKeeper(ContactType userType) {
        return Optional.ofNullable(as(this.sessionKeeperMap.get(userType)));
    }


    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        this.sessionKeeperSettingMap.forEach((name, setting) -> this.sessionFactoryMap.computeIfAbsent(setting.getKeeperFactory(),
                f -> UnitLoader.getLoader(SessionKeeperFactory.class).checkUnit(f)));
    }

    private void notifyOnline(Session session) {
        if (!session.isAuthenticated()) {
            return;
        }
        NetSessionKeeper keeper = sessionKeeperMap.get(session.getContactType());
        if (keeper != null) {
            keeper.notifyOnline(session);
        }
    }

    private void notifyOffline(Session session) {
        if (!session.isAuthenticated()) {
            return;
        }
        NetSessionKeeper keeper = sessionKeeperMap.get(session.getContactType());
        if (keeper != null) {
            keeper.notifyOffline(session);
        }
    }

    private void notifyClose(Session session) {
        if (!session.isAuthenticated()) {
            return;
        }
        NetSessionKeeper keeper = sessionKeeperMap.get(session.getContactType());
        if (keeper != null) {
            keeper.notifyClose(session);
        }
    }

}
