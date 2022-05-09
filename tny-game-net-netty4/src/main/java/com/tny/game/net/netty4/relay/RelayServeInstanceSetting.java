package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.cluster.*;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 3:42 下午
 */
public class RelayServeInstanceSetting extends BaseServeNode {

    public RelayServeInstanceSetting() {
    }

    public RelayServeInstanceSetting(String appType, String scopeType, String serveName, String service,
            NetAccessNode point) {
        super(appType, scopeType, serveName, service, point);
    }

    public RelayServeInstanceSetting(String serveName, String service, String appType, String scopeType, long id, String scheme, String host,
            int port) {
        super(serveName, service, appType, scopeType, id, scheme, host, port);
    }

    @Override
    public RelayServeInstanceSetting setId(long id) {
        super.setId(id);
        return this;
    }

    @Override
    public RelayServeInstanceSetting setHealthy(boolean healthy) {
        super.setHealthy(healthy);
        return this;
    }

    @Override
    public RelayServeInstanceSetting setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    @Override
    public RelayServeInstanceSetting setHost(String host) {
        super.setHost(host);
        return this;
    }

    @Override
    public RelayServeInstanceSetting setPort(int port) {
        super.setPort(port);
        return this;
    }

    @Override
    public RelayServeInstanceSetting setMetadata(Map<String, Object> metadata) {
        super.setMetadata(metadata);
        return this;
    }

    @Override
    public RelayServeInstanceSetting setUrl(String value) {
        super.setUrl(value);
        return this;
    }

}
