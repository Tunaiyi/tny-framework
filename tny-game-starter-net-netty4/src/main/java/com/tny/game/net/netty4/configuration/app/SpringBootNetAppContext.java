package com.tny.game.net.netty4.configuration.app;

import com.tny.game.boot.launcher.*;
import com.tny.game.net.base.*;

import java.util.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 10:52
 */
public class SpringBootNetAppContext extends DefaultNetAppContext {

    public SpringBootNetAppContext(SpringBootNetAppProperties configure) {
        super();
        Set<String> scanPackages = new HashSet<>(ApplicationLauncherContext.getBasePackages());
        scanPackages.addAll(configure.getBasePackages());
        this.setName(configure.getName());
        this.setServerId(configure.getServerId());
        this.setLocale(configure.getLocale());
        this.setAppType(configure.getAppType());
        this.setScopeType(configure.getScopeType());
        this.setScanPackages(scanPackages);
    }

}
