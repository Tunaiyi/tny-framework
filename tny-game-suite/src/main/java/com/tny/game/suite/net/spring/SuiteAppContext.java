package com.tny.game.suite.net.spring;

import com.tny.game.net.base.configuration.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 10:52
 */
public class SuiteAppContext extends DefaultAppContext {

    @Override
    public DefaultAppContext setName(String name) {
        return super.setName(name);
    }

    @Override
    public DefaultAppContext setAppType(String appType) {
        return super.setAppType(appType);
    }

    @Override
    public DefaultAppContext setScopeType(String scopeType) {
        return super.setScopeType(scopeType);
    }

    @Override
    public DefaultAppContext setScanPackages(String[] scanPackages) {
        return super.setScanPackages(scanPackages);
    }

}
