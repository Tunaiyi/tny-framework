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

package com.tny.game.boot.configuration;

import com.tny.game.boot.launcher.*;
import com.tny.game.boot.registrar.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 8:55 下午
 */
@Configuration(proxyBeanMethods = false)
public class TnyFrameworkAutoConfiguration {

    @Bean
    public UnitLoadInitiator unitLoadInitiator() {
        return new UnitLoadInitiator();
    }

    @Bean
    public EventListenerInitiator eventListenerInitiator() {
        return new EventListenerInitiator();
    }

    @Bean
    public ApplicationLauncherLifecycle applicationLauncherLifecycle() {
        return new ApplicationLauncherLifecycle();
    }

}
