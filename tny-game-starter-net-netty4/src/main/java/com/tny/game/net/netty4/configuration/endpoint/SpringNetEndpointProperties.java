package com.tny.game.net.netty4.configuration.endpoint;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.context.properties.*;

import java.util.Map;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 3:45 上午
 */
@ConfigurationProperties(prefix = "tny.net.endpoint")
public class SpringNetEndpointProperties {

	private Map<String, SpringNetSessionKeeperSetting> sessionKeeperSettings = ImmutableMap.of();

	@NestedConfigurationProperty
	private SpringNetSessionKeeperSetting sessionKeeper = new SpringNetSessionKeeperSetting();

	@NestedConfigurationProperty
	private SpringNetTerminalKeeperSetting terminalKeeper = new SpringNetTerminalKeeperSetting();

	private Map<String, SpringNetTerminalKeeperSetting> terminalKeeperSettings = ImmutableMap.of();

	public SpringNetSessionKeeperSetting getSessionKeeper() {
		return this.sessionKeeper;
	}

	public SpringNetEndpointProperties setSessionKeeper(SpringNetSessionKeeperSetting sessionKeeper) {
		this.sessionKeeper = sessionKeeper;
		return this;
	}

	public Map<String, SpringNetSessionKeeperSetting> getSessionKeeperSettings() {
		return this.sessionKeeperSettings;
	}

	public SpringNetEndpointProperties setSessionKeeperSettings(
			Map<String, SpringNetSessionKeeperSetting> sessionKeeperSettings) {
		sessionKeeperSettings.forEach((name, setting) -> setting.setName(ifNotBlankElse(setting.getName(), name)));
		this.sessionKeeperSettings = ImmutableMap.copyOf(sessionKeeperSettings);
		return this;
	}

	public SpringNetTerminalKeeperSetting getTerminalKeeper() {
		return this.terminalKeeper;
	}

	public SpringNetEndpointProperties setTerminalKeeper(
			SpringNetTerminalKeeperSetting terminalKeeper) {
		this.terminalKeeper = terminalKeeper;
		return this;
	}

	public Map<String, SpringNetTerminalKeeperSetting> getTerminalKeeperSettings() {
		return this.terminalKeeperSettings;
	}

	public SpringNetEndpointProperties setTerminalKeeperSettings(Map<String, SpringNetTerminalKeeperSetting> terminalKeeperSettings) {
		terminalKeeperSettings.forEach((name, setting) -> setting.setName(ifNotBlankElse(setting.getName(), name)));
		this.terminalKeeperSettings = ImmutableMap.copyOf(terminalKeeperSettings);
		return this;
	}

}
