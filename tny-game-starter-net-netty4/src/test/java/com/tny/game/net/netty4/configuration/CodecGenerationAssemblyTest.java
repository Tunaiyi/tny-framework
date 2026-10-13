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
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.netty4.network.codec.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 编解码代次装配闭合测试（default-to-mac-generation 生产接线补洞）：
 * UnitLoadInitiator 以 Spring bean 名注册 unit——默认代次所引用的每个 unit 名
 * MUST 存在同名 @Bean 方法且返回类型正确，否则生产启动 "not exist unit"。
 * 未来任何默认翻转若漏注册 bean，本测试先行变红。
 */
class CodecGenerationAssemblyTest {

    private static Set<String> beanMethodNames() {
        Set<String> names = new HashSet<>();
        for (Method m : NetAutoConfiguration.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(org.springframework.context.annotation.Bean.class)) {
                names.add(m.getName());
            }
        }
        return names;
    }

    @Test
    void defaultGenerationUnitsHaveNamedBeans() {
        NetPacketCodecSetting setting = new NetPacketCodecSetting();
        Set<String> beans = beanMethodNames();
        assertTrue(beans.contains(setting.getVerifier()),
                "默认 verifier '" + setting.getVerifier() + "' 必须在 NetAutoConfiguration 有同名 @Bean（UnitLoadInitiator 按 bean 名注册）");
        assertTrue(beans.contains(setting.getCrypto()),
                "默认 crypto '" + setting.getCrypto() + "' 必须在 NetAutoConfiguration 有同名 @Bean");
    }

    @Test
    void beanNamesMatchUnitNameDerivation() throws Exception {
        Map<String, Class<?>> expected = new LinkedHashMap<>();
        expected.put(UnitNames.lowerCamelName(SipHash24CodecVerifier.class), SipHash24CodecVerifier.class);
        expected.put(UnitNames.lowerCamelName(XorTileCodecCrypto.class), XorTileCodecCrypto.class);
        expected.put(UnitNames.lowerCamelName(CRC64CodecVerifier.class), CRC64CodecVerifier.class);
        expected.put(UnitNames.lowerCamelName(Crc32CodecVerifier.class), Crc32CodecVerifier.class);
        expected.put(UnitNames.lowerCamelName(XOrCodecCrypto.class), XOrCodecCrypto.class);
        for (Map.Entry<String, Class<?>> e : expected.entrySet()) {
            Method m = NetAutoConfiguration.class.getDeclaredMethod(e.getKey());
            assertEquals(e.getValue(), m.getReturnType(), "bean " + e.getKey() + " 返回类型必须为 unit 类本身（保 @Unit 名推导一致）");
        }
    }

    @Test
    void noopVerifierAlsoRegistered() {
        Set<String> beans = beanMethodNames();
        assertTrue(beans.contains("noopCodecVerifier"), "显式点名 noopCodecVerifier 的部署需可达（补既有缝隙）");
    }
}
