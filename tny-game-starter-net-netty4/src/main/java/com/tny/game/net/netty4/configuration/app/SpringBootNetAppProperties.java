package com.tny.game.net.netty4.configuration.app;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/14 6:30 下午
 */
@ConfigurationProperties("tny.app")
public class SpringBootNetAppProperties {

    private String name;

    private int serverId;

    private String appType = "default";

    private String scopeType = "online";

    private String locale = "zh-CN";

    private List<String> basePackages = ImmutableList.of();

    public int getServerId() {
        return this.serverId;
    }

    public SpringBootNetAppProperties setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public SpringBootNetAppProperties setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocale() {
        return this.locale;
    }

    public SpringBootNetAppProperties setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public String getAppType() {
        return this.appType;
    }

    public SpringBootNetAppProperties setAppType(String appType) {
        this.appType = appType;
        return this;
    }

    public String getScopeType() {
        return this.scopeType;
    }

    public SpringBootNetAppProperties setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    public List<String> getBasePackages() {
        return this.basePackages;
    }

    public SpringBootNetAppProperties setBasePackages(List<String> basePackages) {
        this.basePackages = ImmutableList.copyOf(basePackages);
        return this;
    }

}
