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

package com.tny.game.net.netty4.configuration.filter;

import com.tny.game.common.io.word.*;
import com.tny.game.net.command.plugins.filter.text.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;


/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "tny.net.filter.text-filter.enable", havingValue = "true")
@EnableConfigurationProperties(TextFilterProperties.class)
public class TextFilterAutoConfiguration {

    @Autowired
    private TextFilterProperties textFilterProperties;

    @Bean
    public WordsFilter wordsFilter() throws Exception {
        LocalWordsFilter filter = new LocalWordsFilter(this.textFilterProperties.getFile(), this.textFilterProperties.getHideSymbol());
        filter.load();
        return filter;
    }

    @Bean
    public TextCheckFilter textCheckFilter(ObjectProvider<WordsFilter> wordsFilters) {
        // 原实现忽略入参（词表永远不生效）且 required List 注入在零候选时炸启动；
        // 统一前缀开关后以 ObjectProvider 收集，缺词表时长度检查仍可用（内容检查需配置词表）
        return new TextCheckFilter().setWordsFilters(wordsFilters.orderedStream().toList());
    }

}
