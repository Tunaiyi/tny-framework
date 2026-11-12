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
package com.tny.game.net.netty4.configuration;

import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.netty4.configuration.processor.*;
import com.tny.game.net.netty4.network.guide.*;
import com.tny.game.net.netty4.relay.cluster.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 24（Wave-C）红灯基线：装配语义——同名 bean 双路注册收口、发现开关自洽、
 * 空值配置绑定不 NPE、APM 集成具备类条件（net-boot-integration 契约）。
 */
class BootAssemblySemanticsTest {

    @Test
    @DisplayName("executor registrar 不再覆盖既有同名 bean 定义")
    void registrarSkipsExistingBeanDefinition() throws Exception {
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        BeanDefinition sentinel = BeanDefinitionBuilder.genericBeanDefinition(Object.class).getBeanDefinition();
        registry.registerBeanDefinition("defaultCommandExecutorFactory", sentinel);

        ImportCommandExecutorFactoryBeanDefinitionRegistrar registrar = new ImportCommandExecutorFactoryBeanDefinitionRegistrar();
        Method load = ImportCommandExecutorFactoryBeanDefinitionRegistrar.class
                .getDeclaredMethod("loadBeanDefinition", String.class, SerialCommandExecutorSetting.class, BeanDefinitionRegistry.class);
        load.setAccessible(true);
        SerialCommandExecutorSetting enabled = new SerialCommandExecutorSetting().setEnable(true);
        load.invoke(registrar, "default", enabled, registry);

        assertSame(sentinel, registry.getBeanDefinition("defaultCommandExecutorFactory"),
                "双路注册必须先到先得（修复前 registrar 覆盖 @Bean 定义，本断言应红）");
    }

    @Test
    @DisplayName("discovery:false 显式关闭必须生效（serveName 不得反向强制开启）")
    void discoverySwitchIsAuthoritative() {
        SpringRelayServeClusterSetting setting = new SpringRelayServeClusterSetting();
        setting.setDiscovery(false);
        setting.setServeName("some-service");
        assertFalse(setting.isDiscovery(), "修复前 serveName 非空强制 true（本断言应红）");

        setting.setDiscovery(true);
        assertTrue(setting.isDiscovery());
    }

    @Test
    @DisplayName("`server: ~` 空值绑定不再 NPE 击穿属性装配")
    void nullNestedPropertiesAreTolerated() {
        SpringBootNetBootstrapProperties properties = new SpringBootNetBootstrapProperties();
        assertDoesNotThrow(() -> properties.setServer(null));
        assertDoesNotThrow(() -> properties.setClient(null));
    }

}
