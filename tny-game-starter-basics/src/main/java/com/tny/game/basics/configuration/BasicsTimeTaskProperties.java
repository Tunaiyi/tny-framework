package com.tny.game.basics.configuration;

import com.tny.game.common.scheduler.*;
import org.springframework.boot.context.properties.*;

import java.util.*;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:23 下午
 */
@ConfigurationProperties(BASICS_TIME_TASK)
public class BasicsTimeTaskProperties implements TimeTaskSchemesSetting {

	private boolean enable = false;

	private int id;

	private int maxTaskSize = 3000;

	private List<DefaultTimeTaskScheme> schemes = new ArrayList<>();

	@NestedConfigurationProperty
	private TimeTaskPluginSetting plugin = new TimeTaskPluginSetting();

	public boolean isEnable() {
		return enable;
	}

	public BasicsTimeTaskProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public int getId() {
		return id;
	}

	public BasicsTimeTaskProperties setId(int id) {
		this.id = id;
		return this;
	}

	public List<DefaultTimeTaskScheme> getSchemes() {
		return schemes;
	}

	public BasicsTimeTaskProperties setSchemes(List<DefaultTimeTaskScheme> schemes) {
		this.schemes = schemes;
		return this;
	}

	public TimeTaskPluginSetting getPlugin() {
		return plugin;
	}

	public BasicsTimeTaskProperties setPlugin(TimeTaskPluginSetting plugin) {
		this.plugin = plugin;
		return this;
	}

	public int getMaxTaskSize() {
		return maxTaskSize;
	}

	public BasicsTimeTaskProperties setMaxTaskSize(int maxTaskSize) {
		this.maxTaskSize = maxTaskSize;
		return this;
	}

	@Override
	public List<TimeTaskScheme> getTimeTaskSchemeList() {
		return new ArrayList<>(schemes);
	}

}
