/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:23 下午
 */
@ConfigurationProperties(BasicsPropertiesConstants.BASICS_CAPACITY_MODULE)
public class BasicsCapacityModuleProperties {

    private boolean enable = false;

    public boolean isEnable() {
        return enable;
    }

    public BasicsCapacityModuleProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

}
