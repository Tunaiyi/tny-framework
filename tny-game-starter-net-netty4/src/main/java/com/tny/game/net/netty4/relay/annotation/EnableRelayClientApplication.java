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
package com.tny.game.net.netty4.relay.annotation;

import com.tny.game.net.netty4.configuration.*;
import com.tny.game.net.netty4.relay.cluster.*;
import com.tny.game.net.netty4.relay.configuration.*;
import com.tny.game.net.netty4.relay.guide.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 14:15
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({
        NetAutoConfiguration.class,
        RelayClientAutoConfiguration.class,
})
@Import({
        ImportRelayBootstrapDefinitionRegistrar.class,
        ImportRelayServeClusterBootstrapDefinitionRegistrar.class,
})
public @interface EnableRelayClientApplication {

}
