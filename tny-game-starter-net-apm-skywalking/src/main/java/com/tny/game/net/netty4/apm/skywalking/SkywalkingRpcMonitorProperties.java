package com.tny.game.net.netty4.apm.skywalking;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.net.netty4.apm.skywalking.SkywalkingPropertiesConstants.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/2/9 04:34
 **/
@ConfigurationProperties(prefix = SKYWALKING_PREFIX)
public class SkywalkingRpcMonitorProperties {

    private boolean enable = true;

    private boolean enableCollectArguments = true;

    private int collectArgumentsMaxLength = 125;

    public boolean isDisable() {
        return !enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public SkywalkingRpcMonitorProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public boolean isEnableCollectArguments() {
        return enableCollectArguments;
    }

    public SkywalkingRpcMonitorProperties setEnableCollectArguments(boolean enableCollectArguments) {
        this.enableCollectArguments = enableCollectArguments;
        return this;
    }

    public int getCollectArgumentsMaxLength() {
        return collectArgumentsMaxLength;
    }

    public SkywalkingRpcMonitorProperties setCollectArgumentsMaxLength(int collectArgumentsMaxLength) {
        this.collectArgumentsMaxLength = collectArgumentsMaxLength;
        return this;
    }

}
