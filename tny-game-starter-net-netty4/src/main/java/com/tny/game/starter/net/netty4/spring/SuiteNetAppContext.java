package com.tny.game.starter.net.netty4.spring;

import com.tny.game.net.base.configuration.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 10:52
 */
public class SuiteNetAppContext extends DefaultNetAppContext {

    @Override
    public DefaultNetAppContext setName(String name) {
        return super.setName(name);
    }

    @Override
    public DefaultNetAppContext setAppType(String appType) {
        return super.setAppType(appType);
    }

    @Override
    public DefaultNetAppContext setScopeType(String scopeType) {
        return super.setScopeType(scopeType);
    }

    @Override
    public DefaultNetAppContext setScanPackages(String[] scanPackages) {
        return super.setScanPackages(scanPackages);
    }

}
