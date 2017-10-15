package com.tny.game.suite.cluster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tny.game.common.utils.URL;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@ProtoEx(SuiteProtoIDs.CLUSTER_$URL_WEB_SERVICE_NODE)
public class ServiceNode {

    @ProtoExField(1)
    private int serverID;

    @ProtoExField(2)
    private List<String> urlStrings = ImmutableList.of();

    @ProtoExField(3)
    private String appType;

    private volatile Map<String, URL> urlMap = ImmutableMap.of();

    public ServiceNode() {
    }

    public ServiceNode(String appType, int serverID, String... urls) {
        this.appType = appType;
        this.serverID = serverID;
        this.urlStrings = Arrays.asList(urls);
    }

    public int getServerID() {
        return this.serverID;
    }

    public String getAppType() {
        return appType;
    }

    public URL getURL(String protocol) {
        return this.getUrlMap().get(protocol);
    }

    public Map<String, URL> getUrlMap() {
        if (this.urlMap.size() != this.urlStrings.size()) {
            ImmutableMap.Builder<String, URL> urlMap = ImmutableMap.builder();
            for (String value : this.urlStrings) {
                URL url = URL.valueOf(value);
                urlMap.put(url.getProtocol(), url);
            }
            this.urlMap = urlMap.build();
        }
        return this.urlMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.serverID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        ServiceNode other = (ServiceNode) obj;
        if (this.serverID != other.serverID)
            return false;
        return true;
    }

}
