package com.tny.game.net.endpoint;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.event.bus.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.transport.*;
import org.apache.commons.collections4.MapUtils;

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

	private static final String DEFAULT_KEY = "default";

	private final Map<String, EndpointKeeper<?, ?>> endpointKeeperMap = new ConcurrentHashMap<>();

	private Map<String, SessionKeeperSetting> sessionKeeperSettingMap = ImmutableMap.of();

	private Map<String, TerminalKeeperSetting> terminalKeeperSettingMap = ImmutableMap.of();

	private final Map<String, SessionKeeperFactory<?, SessionKeeperSetting>> sessionFactoryMap = new ConcurrentHashMap<>();

	private final Map<String, TerminalKeeperFactory<?, TerminalKeeperSetting>> terminalFactoryMap = new ConcurrentHashMap<>();

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static final BindVoidEventBus<EndpointKeeperCreateListener, EndpointKeeper> ON_CREATE =
			EventBuses.of(EndpointKeeperCreateListener.class, EndpointKeeperCreateListener::onCreate);

	public CommonEndpointKeeperManager() {
	}

	public CommonEndpointKeeperManager(
			SessionKeeperSetting defaultSessionKeeperSetting,
			TerminalKeeperSetting defaultTerminalKeeperSetting,
			Map<String, ? extends SessionKeeperSetting> sessionKeeperSettingMap,
			Map<String, ? extends TerminalKeeperSetting> terminalKeeperSettingMap) {
		Map<String, SessionKeeperSetting> sessionSettingMap = new HashMap<>();
		Map<String, TerminalKeeperSetting> terminalSettingMap = new HashMap<>();
		if (MapUtils.isNotEmpty(sessionKeeperSettingMap)) {
			sessionKeeperSettingMap.forEach((name, setting) -> sessionSettingMap.put(ifNotBlankElse(setting.getName(), name), setting));
		}
		if (MapUtils.isNotEmpty(terminalKeeperSettingMap)) {
			terminalKeeperSettingMap.forEach((name, setting) -> terminalSettingMap.put(ifNotBlankElse(setting.getName(), name), setting));
		}
		if (defaultSessionKeeperSetting != null) {
			sessionSettingMap.put(ifNotBlankElse(defaultSessionKeeperSetting.getName(), DEFAULT_KEY), defaultSessionKeeperSetting);
		}
		if (defaultTerminalKeeperSetting != null) {
			terminalSettingMap.put(ifNotBlankElse(defaultTerminalKeeperSetting.getName(), DEFAULT_KEY), defaultTerminalKeeperSetting);
		}
		this.sessionKeeperSettingMap = ImmutableMap.copyOf(sessionSettingMap);
		this.terminalKeeperSettingMap = ImmutableMap.copyOf(terminalSettingMap);
	}

	private EndpointKeeper<?, ?> create(String userType, TunnelMode tunnelMode) {
		if (tunnelMode == TunnelMode.SERVER) {
			SessionKeeperSetting setting = this.sessionKeeperSettingMap.get(userType);
			if (setting == null) {
				setting = this.sessionKeeperSettingMap.get(DEFAULT_KEY);
			}
			return create(userType, tunnelMode, setting, this.sessionFactoryMap.get(setting.getKeeperFactory()));
		} else {
			TerminalKeeperSetting setting = this.terminalKeeperSettingMap.get(userType);
			if (setting == null) {
				setting = this.terminalKeeperSettingMap.get(DEFAULT_KEY);
			}
			return create(userType, tunnelMode, setting, this.terminalFactoryMap.get(setting.getKeeperFactory()));
		}
	}

	private <E extends Endpoint<?>, EK extends EndpointKeeper<?, E>, S extends EndpointKeeperSetting> EK create(
			String userType, TunnelMode endpointType, S setting, EndpointKeeperFactory<?, EK, S> factory) {
		Asserts.checkNotNull(factory, "can not found {}.{} session factory", endpointType, userType);
		return factory.createKeeper(userType, setting);
	}

	@Override
	public <UID, K extends EndpointKeeper<UID, ? extends Endpoint<UID>>> K loadOrCreate(String userType, TunnelMode tunnelMode) {
		EndpointKeeper<?, ?> keeper = this.endpointKeeperMap.get(userType);
		if (keeper != null) {
			return as(keeper);
		}
		EndpointKeeper<?, ?> newOne = create(userType, tunnelMode);
		keeper = as(this.endpointKeeperMap.computeIfAbsent(userType, (k) -> newOne));
		if (keeper == newOne) {
			ON_CREATE.notify(keeper);
		}
		return as(keeper);
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
			this.sessionFactoryMap
					.computeIfAbsent(setting.getKeeperFactory(), f -> UnitLoader.getLoader(SessionKeeperFactory.class).checkUnit(f));
		});
		this.terminalKeeperSettingMap.forEach((name, setting) -> {
			this.terminalFactoryMap
					.computeIfAbsent(setting.getKeeperFactory(), f -> UnitLoader.getLoader(TerminalKeeperFactory.class).checkUnit(f));
		});
		//        UnitLoader.getLoader(SessionKeeperSetting.class).getAllUnits().forEach(unit -> {
		//            this.sessionKeeperSettingMap.put(unit.getName(), as(unit));
		//            this.sessionFactoryMap
		//                    .computeIfAbsent(unit.getKeeperFactory(), f -> UnitLoader.getLoader(SessionKeeperFactory.class).getUnitAnCheck(f));
		//        });
		//        UnitLoader.getLoader(TerminalKeeperSetting.class).getAllUnits().forEach(unit -> {
		//            this.terminalKeeperSettingMap.put(unit.getName(), as(unit));
		//            this.terminalFactoryMap
		//                    .computeIfAbsent(unit.getKeeperFactory(), f -> UnitLoader.getLoader(TerminalKeeperFactory.class).getUnitAnCheck(f));
		//        });
	}

}
