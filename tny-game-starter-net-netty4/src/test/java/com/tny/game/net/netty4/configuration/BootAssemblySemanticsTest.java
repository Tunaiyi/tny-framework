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
