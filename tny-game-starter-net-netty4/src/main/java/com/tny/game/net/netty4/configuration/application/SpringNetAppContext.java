/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.net.netty4.configuration.application;

import com.tny.game.boot.launcher.*;
import com.tny.game.net.application.*;

import java.util.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 10:52
 */
public class SpringNetAppContext extends DefaultNetAppContext {

    public SpringNetAppContext(SpringNetAppProperties configure) {
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
