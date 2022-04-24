package com.tny.game.net.relay.cluster;

import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.url.*;

import java.util.*;

/**
 * 集群节点信息
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:21 下午
 */
public class BaseNetAccessPoint implements NetAccessPoint {

    private long id;

    private String scheme;

    private String host;

    private int port;

    private ObjectMap metadata = new ObjectMap();

    private boolean healthy = true;

    public BaseNetAccessPoint() {
    }

    public BaseNetAccessPoint(NetAccessPoint point) {
        this.id = point.getId();
        this.scheme = point.getScheme();
        this.host = point.getHost();
        this.port = point.getPort();
        this.metadata.putAll(point.getMetadata());
        this.healthy = point.isHealthy();
    }

    public BaseNetAccessPoint(long id, String scheme, String host, int port, boolean healthy) {
        this.id = id;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.healthy = healthy;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public boolean isHealthy() {
        return healthy;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public MapAccessor metadata() {
        return metadata;
    }

    protected BaseNetAccessPoint setId(long id) {
        this.id = id;
        return this;
    }

    protected BaseNetAccessPoint setHealthy(boolean healthy) {
        this.healthy = healthy;
        return this;
    }

    protected BaseNetAccessPoint setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    protected BaseNetAccessPoint setHost(String host) {
        this.host = host;
        return this;
    }

    protected BaseNetAccessPoint setPort(int port) {
        this.port = port;
        return this;
    }

    protected BaseNetAccessPoint setMetadata(Map<String, Object> metadata) {
        this.metadata = new ObjectMap(metadata);
        return this;
    }

    protected BaseNetAccessPoint setUrl(String value) {
        URL url = URL.valueOf(value);
        this.setScheme(url.getScheme())
                .setHost(url.getHost())
                .setPort(url.getPort());
        return this;
    }

}
