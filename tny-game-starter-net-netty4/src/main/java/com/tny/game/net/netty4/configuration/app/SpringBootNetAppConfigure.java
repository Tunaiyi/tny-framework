package com.tny.game.net.netty4.configuration.app;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/14 6:30 下午
 */
@Configuration
@ConfigurationProperties("tny.app")
public class SpringBootNetAppConfigure {

    private String name;

    private int serverId;

    private String appType = "default";

    private String scopeType = "online";

    private String locale = "zh-CN";

    private List<String> basePackages;

    public int getServerId() {
        return this.serverId;
    }

    public SpringBootNetAppConfigure setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public SpringBootNetAppConfigure setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocale() {
        return this.locale;
    }

    public SpringBootNetAppConfigure setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public String getAppType() {
        return this.appType;
    }

    public SpringBootNetAppConfigure setAppType(String appType) {
        this.appType = appType;
        return this;
    }

    public String getScopeType() {
        return this.scopeType;
    }

    public SpringBootNetAppConfigure setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    public List<String> getBasePackages() {
        return this.basePackages;
    }

    public SpringBootNetAppConfigure setBasePackages(List<String> basePackages) {
        this.basePackages = ImmutableList.copyOf(basePackages);
        return this;
    }

}
