package com.tny.game.net.netty4.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.rmi.RmiClientInterceptor;
import org.springframework.remoting.support.RemoteAccessor;

import java.util.concurrent.ConcurrentHashMap;

public class RmiProxyFactory<T> extends RemoteAccessor {

    private ConcurrentHashMap<String, Object> urlProxyMap = new ConcurrentHashMap<>();

    private boolean lookupStubOnStartup = true;

    private boolean cacheStub = true;

    private boolean refreshStubOnConnectFailure = false;

    /**
     * Set whether to look up the RMI stub on startup. Default is "true".
     * <p>Can be turned off to allow for late start of the RMI server.
     * In this case, the RMI stub will be fetched on first access.
     *
     * @see #setCacheStub
     */
    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    /**
     * Set whether to cache the RMI stub once it has been located.
     * Default is "true".
     * <p>Can be turned off to allow for hot restart of the RMI server.
     * In this case, the RMI stub will be fetched for each invocation.
     *
     * @see #setLookupStubOnStartup
     */
    public void setCacheStub(boolean cacheStub) {
        this.cacheStub = cacheStub;
    }

    /**
     * Set whether to refresh the RMI stub on connect failure.
     * Default is "false".
     * <p>Can be turned on to allow for hot restart of the RMI server.
     * If a cached RMI stub throws an RMI exception that indicates a
     * remote connect failure, a fresh proxy will be fetched and the
     * invocation will be retried.
     *
     * @see java.rmi.ConnectException
     * @see java.rmi.ConnectIOException
     * @see java.rmi.NoSuchObjectException
     */
    public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }

    @SuppressWarnings("unchecked")
    public T getRmiProxy(String serviceUrl) {
        Object proxy = this.urlProxyMap.get(serviceUrl);
        if (proxy == null) {
            RmiClientInterceptor interceptor = new RmiClientInterceptor();
            interceptor.setBeanClassLoader(this.getBeanClassLoader());
            interceptor.setCacheStub(this.cacheStub);
            interceptor.setLookupStubOnStartup(this.lookupStubOnStartup);
            interceptor.setRefreshStubOnConnectFailure(this.refreshStubOnConnectFailure);
            interceptor.setServiceInterface(this.getServiceInterface());
            interceptor.setServiceUrl(serviceUrl);
            interceptor.afterPropertiesSet();
            proxy = new ProxyFactory(this.getServiceInterface(), interceptor).getProxy(this.getBeanClassLoader());
            this.urlProxyMap.putIfAbsent(serviceUrl, proxy);
            proxy = this.urlProxyMap.get(serviceUrl);
        }
        return (T) proxy;
    }

    public void clear() {
        this.urlProxyMap.clear();
    }

}
