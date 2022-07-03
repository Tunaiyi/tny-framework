package com.tny.game.namespace.etcd;

import java.util.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 02:52
 **/
public class EtcdConfig {

    private long leaseTtl = 5;

    private long leaseId = -1;

    private String charset = "UTF-8";

    private boolean leaseEnable = false;

    private List<String> endpoints;

    private String user;

    private String password;

    private String namespace = "";

    private String authority;

    private String loadBalancerPolicy;

    private Integer maxInboundMessageSize;

    private long retryDelay = 500;

    private long retryMaxDelay = 2500;

    private long keepaliveTime = 30000;

    private long keepaliveTimeout = 100000;

    private boolean keepaliveWithoutCalls = true;

    private Long retryMaxDuration;

    private Long connectTimeout;

    private boolean waitForReady = true;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> authHeaders = new HashMap<>();

    public long getLeaseTtl() {
        return leaseTtl;
    }

    public long getLeaseId() {
        return leaseId;
    }

    public boolean isLeaseEnable() {
        return leaseEnable;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public String getCharset() {
        return charset;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getAuthority() {
        return authority;
    }

    public String getLoadBalancerPolicy() {
        return loadBalancerPolicy;
    }

    public Integer getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public long getRetryMaxDelay() {
        return retryMaxDelay;
    }

    public long getKeepaliveTime() {
        return keepaliveTime;
    }

    public long getKeepaliveTimeout() {
        return keepaliveTimeout;
    }

    public boolean isKeepaliveWithoutCalls() {
        return keepaliveWithoutCalls;
    }

    public Long getRetryMaxDuration() {
        return retryMaxDuration;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public boolean isWaitForReady() {
        return waitForReady;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getAuthHeaders() {
        return authHeaders;
    }

    public EtcdConfig setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public EtcdConfig setLeaseTtl(long leaseTtl) {
        this.leaseTtl = leaseTtl;
        return this;
    }

    public EtcdConfig setLeaseId(long leaseId) {
        this.leaseId = leaseId;
        return this;
    }

    public EtcdConfig setLeaseEnable(boolean leaseEnable) {
        this.leaseEnable = leaseEnable;
        return this;
    }

    public EtcdConfig setEndpoints(String... endpoints) {
        this.endpoints = new ArrayList<>(Arrays.asList(endpoints));
        return this;
    }

    public EtcdConfig setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
        return this;
    }

    public EtcdConfig setUser(String user) {
        this.user = user;
        return this;
    }

    public EtcdConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public EtcdConfig setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public EtcdConfig setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

    public EtcdConfig setLoadBalancerPolicy(String loadBalancerPolicy) {
        this.loadBalancerPolicy = loadBalancerPolicy;
        return this;
    }

    public EtcdConfig setMaxInboundMessageSize(Integer maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
        return this;
    }

    public EtcdConfig setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
        return this;
    }

    public EtcdConfig setRetryMaxDelay(long retryMaxDelay) {
        this.retryMaxDelay = retryMaxDelay;
        return this;
    }

    public EtcdConfig setKeepaliveTime(long keepaliveTime) {
        this.keepaliveTime = keepaliveTime;
        return this;
    }

    public EtcdConfig setKeepaliveTimeout(long keepaliveTimeout) {
        this.keepaliveTimeout = keepaliveTimeout;
        return this;
    }

    public EtcdConfig setKeepaliveWithoutCalls(boolean keepaliveWithoutCalls) {
        this.keepaliveWithoutCalls = keepaliveWithoutCalls;
        return this;
    }

    public EtcdConfig setRetryMaxDuration(Long retryMaxDuration) {
        this.retryMaxDuration = retryMaxDuration;
        return this;
    }

    public EtcdConfig setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public EtcdConfig setWaitForReady(boolean waitForReady) {
        this.waitForReady = waitForReady;
        return this;
    }

    public EtcdConfig setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public EtcdConfig setAuthHeaders(Map<String, String> authHeaders) {
        this.authHeaders = authHeaders;
        return this;
    }

}
