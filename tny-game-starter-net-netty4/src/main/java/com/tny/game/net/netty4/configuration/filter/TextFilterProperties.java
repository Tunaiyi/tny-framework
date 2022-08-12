/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.configuration.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@ConfigurationProperties("tny.net.filter.text-filter")
public class TextFilterProperties {

    private static final String DEFAULT_FILE = "words.txt";

    private static final String DEFAULT_HIDE_SYMBOL = "*";

    private String file = DEFAULT_FILE;

    private String hideSymbol = DEFAULT_HIDE_SYMBOL;

    public String getFile() {
        return this.file;
    }

    public TextFilterProperties setFile(String file) {
        this.file = file;
        return this;
    }

    public String getHideSymbol() {
        return this.hideSymbol;
    }

    public TextFilterProperties setHideSymbol(String hideSymbol) {
        this.hideSymbol = hideSymbol;
        return this;
    }

}
