/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.cluster;

import org.apache.commons.lang3.builder.*;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class BaseServeNode extends BaseNetAccessNode implements ServeNode {

    private String appType;

    private String scopeType;

    private String serveName;

    private String service;

    public BaseServeNode() {
        super();
    }

    public BaseServeNode(String serveName, String service, NetAccessNode point) {
        super(point);
        this.serveName = serveName;
        this.service = service;
    }

    public BaseServeNode(String appType, String scopeType, String serveName, String service, NetAccessNode point) {
        super(point);
        this.appType = appType;
        this.scopeType = scopeType;
        this.serveName = serveName;
        this.service = service;
    }

    public BaseServeNode(String serveName, String service, String appType, String scopeType, long id, String scheme, String host, int port) {
        super(id, scheme, host, port, true);
        this.serveName = serveName;
        this.appType = appType;
        this.scopeType = scopeType;
        this.service = service;
    }

    @Override
    public String getServeName() {
        return serveName;
    }

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public String getScopeType() {
        return scopeType;
    }

    @Override
    public String getService() {
        return service;
    }

    protected BaseServeNode setServeName(String serveName) {
        this.serveName = serveName;
        return this;
    }

    protected BaseServeNode setService(String service) {
        this.service = service;
        return this;
    }

    protected BaseServeNode setAppType(String appType) {
        this.appType = appType;
        return this;
    }

    protected BaseServeNode setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    @Override
    protected BaseServeNode setId(long id) {
        super.setId(id);
        return this;
    }

    @Override
    protected BaseServeNode setHealthy(boolean healthy) {
        super.setHealthy(healthy);
        return this;
    }

    @Override
    protected BaseServeNode setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    @Override
    protected BaseServeNode setHost(String host) {
        super.setHost(host);
        return this;
    }

    @Override
    protected BaseServeNode setPort(int port) {
        super.setPort(port);
        return this;
    }

    @Override
    protected BaseServeNode setMetadata(Map<String, Object> metadata) {
        super.setMetadata(metadata);
        return this;
    }

    @Override
    protected BaseServeNode setUrl(String value) {
        super.setUrl(value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseServeNode)) {
            return false;
        }

        BaseServeNode that = (BaseServeNode) o;

        return new EqualsBuilder().append(getId(), that.getId()).append(getServeName(), that.getServeName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getServeName()).append(getId()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("serveName", serveName)
                .append("id", getScheme())
                .append("healthy", isHealthy())
                .append("scheme", getScheme())
                .append("host", getHost())
                .append("port", getPort())
                .toString();
    }

}
