package com.tny.game.suite.cluster;

import com.google.common.collect.*;
import com.tny.game.common.url.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.util.*;

@ProtoEx(SuiteProtoIDs.CLUSTER_$URL_WEB_SERVICE_NODE)
public class ServiceNode {

    @ProtoExField(1)
    private int serverId;

    @ProtoExField(2)
    private List<String> urlStrings = ImmutableList.of();

    @ProtoExField(3)
    private String appType;

    private volatile Map<String, URL> urlMap = ImmutableMap.of();

    public ServiceNode() {
    }

    public ServiceNode(String appType, int serverId, String... urls) {
        this.appType = appType;
        this.serverId = serverId;
        this.urlStrings = Arrays.asList(urls);
    }

    public int getServerId() {
        return this.serverId;
    }

    public String getAppType() {
        return this.appType;
    }

    public URL getURL(String protocol) {
        return this.getUrlMap().get(protocol);
    }

    public Map<String, URL> getUrlMap() {
        if (this.urlMap.size() != this.urlStrings.size()) {
            ImmutableMap.Builder<String, URL> urlMap = ImmutableMap.builder();
            for (String value : this.urlStrings) {
                URL url = URL.valueOf(value);
                urlMap.put(url.getScheme(), url);
            }
            this.urlMap = urlMap.build();
        }
        return this.urlMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.serverId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ServiceNode other = (ServiceNode) obj;
        if (this.serverId != other.serverId) {
            return false;
        }
        return true;
    }

}
