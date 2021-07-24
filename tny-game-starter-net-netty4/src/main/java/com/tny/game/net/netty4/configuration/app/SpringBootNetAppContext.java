package com.tny.game.net.netty4.configuration.app;

import com.tny.game.net.base.configuration.*;

import java.util.List;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 10:52
 */
public class SpringBootNetAppContext extends DefaultNetAppContext {

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
    public DefaultNetAppContext setScanPackages(List<String> scanPackages) {
        return super.setScanPackages(scanPackages);
    }

}
