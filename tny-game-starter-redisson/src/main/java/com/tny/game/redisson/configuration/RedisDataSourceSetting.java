/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.redisson.configuration;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/14 7:55 下午
 */
public class RedisDataSourceSetting extends RedisProperties {

    private boolean templateEnable = false;

    private boolean stringTemplateEnable = false;

    public boolean isTemplateEnable() {
        return templateEnable;
    }

    public RedisDataSourceSetting setTemplateEnable(boolean templateEnable) {
        this.templateEnable = templateEnable;
        return this;
    }

    public boolean isStringTemplateEnable() {
        return stringTemplateEnable;
    }

    public RedisDataSourceSetting setStringTemplateEnable(boolean stringTemplateEnable) {
        this.stringTemplateEnable = stringTemplateEnable;
        return this;
    }

}
