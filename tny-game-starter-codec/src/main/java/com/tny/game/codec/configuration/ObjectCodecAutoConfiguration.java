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

package com.tny.game.codec.configuration;

import com.tny.game.codec.*;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration(proxyBeanMethods = false)
public class ObjectCodecAutoConfiguration {

    @Bean
    public ObjectCodecAdapter objectCodecAdapter(List<ObjectCodecFactory> codecFactories) {
        return new ObjectCodecAdapter(codecFactories);
    }

    @Bean
    public ObjectCodecService objectCodecService(ObjectCodecAdapter objectCodecMatcher) {
        return new ObjectCodecService(objectCodecMatcher);
    }

}
