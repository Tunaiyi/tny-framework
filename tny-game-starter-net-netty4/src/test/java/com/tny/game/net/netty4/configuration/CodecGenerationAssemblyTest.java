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
