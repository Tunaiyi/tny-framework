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

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.codec.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 编解码代次 SPI 实现全集 → @Bean 覆盖契约（枚举式护栏，补 CodecGenerationAssemblyTest
 * 硬编码清单防不住"新增实现漏注册"的盲区——crc32CodecVerifier 漏网即为实证）：
 * classpath 扫描 CodecVerifier/CodecCrypto 的全部具体实现（basePackage 用
 * "com.tny.game" 而非仅 com.tny.game.net.codec——防未来实现落错包逃逸），
 * 每个实现 MUST 在 NetAutoConfiguration 存在名字恰为 UnitNames.lowerCamelName(实现类)
 * 的无参 @Bean 方法且返回类型为实现类本身。
 * 违反后果：UnitLoadInitiator 按 Spring bean 名登记 unit，无 bean ⇒ UnitLoader 无该条目 ⇒
 * NetPacketV1Codec.prepareStart 的 checkUnit 启动即 "is not exist unit"（与 verifyEnable 无关）。
 * 返回类型钉死为具体类：派生 simpleName/FQCN 别名与 bean 名兜底注册均以 bean 实际类型为准，
 * 返回接口/父类会让 @Unit 名推导漂移。
 */
class CodecSpiBeanCoverageTest {

    private static final String SCAN_BASE_PACKAGE = "com.tny.game";

    @Test
    void everyCodecVerifierImplementationHasLowerCamelNamedBean() {
        assertSpiFullyWired(CodecVerifier.class);
    }

    @Test
    void everyCodecCryptoImplementationHasLowerCamelNamedBean() {
        assertSpiFullyWired(CodecCrypto.class);
    }

    private static void assertSpiFullyWired(Class<?> spi) {
        Set<Class<?>> implementations = scanImplementations(spi);
        assertFalse(implementations.isEmpty(),
                "classpath 扫描未发现 " + spi.getName() + " 的任何具体实现——扫描配置本身失效，护栏失去意义");
        for (Class<?> implementation : implementations) {
            String beanName = UnitNames.lowerCamelName(implementation);
            Method method;
            try {
                method = NetAutoConfiguration.class.getDeclaredMethod(beanName);
            } catch (NoSuchMethodException e) {
                fail("新增代次实现 " + implementation.getName() + " 必须在 NetAutoConfiguration 有名为 "
                        + beanName + "（=UnitNames.lowerCamelName 推导值）的无参 @Bean，否则 Spring 部署下 "
                        + "UnitLoadInitiator 登记不到该 unit，NetPacketV1Codec.prepareStart 的 checkUnit 启动即"
                        + " 'is not exist unit'（无论 verifyEnable 与否）");
                continue;
            }
            assertTrue(method.isAnnotationPresent(Bean.class),
                    beanName + " 方法必须标注 @Bean，否则不会成为 Spring bean、进不了 UnitLoadInitiator 的发现面");
            assertEquals(implementation, method.getReturnType(),
                    "bean " + beanName + " 返回类型必须为实现类本身（保 @Unit 派生名与 bean 名一致，先例见 CodecGenerationAssemblyTest）");
        }
    }

    /**
     * 用 AssignableTypeFilter 而非 AnnotationTypeFilter(@Unit) 枚举——NoopCodecVerifier 类上没有
     * @Unit，注解过滤器会静默漏掉它；provider 默认 isCandidateComponent 已排除接口与抽象类。
     */
    private static Set<Class<?>> scanImplementations(Class<?> spi) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(spi));
        Set<Class<?>> implementations = new TreeSet<>(Comparator.comparing(Class::getName));
        for (BeanDefinition candidate : provider.findCandidateComponents(SCAN_BASE_PACKAGE)) {
            Class<?> implementation;
            try {
                implementation = Class.forName(candidate.getBeanClassName());
            } catch (ClassNotFoundException e) {
                fail("扫描出的候选 " + candidate.getBeanClassName() + " 无法加载——测试 classpath 异常");
                continue;
            }
            assertTrue(spi.isAssignableFrom(implementation),
                    implementation.getName() + " 必须确实实现 " + spi.getName() + "（过滤器判定与类加载结果不一致）");
            implementations.add(implementation);
        }
        return implementations;
    }
}
