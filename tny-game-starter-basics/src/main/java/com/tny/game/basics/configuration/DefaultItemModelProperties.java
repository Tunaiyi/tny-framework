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
package com.tny.game.basics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;
import static com.tny.game.basics.develop.ItemModelPaths.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/24 1:09 下午
 */
@ConfigurationProperties(BASICS_ITEM_DEFAULT_MODEL)
public class DefaultItemModelProperties {

    private String[] paths = {DEFAULT_ITEM_MODEL_CONFIG_PATH};

    public String[] getPaths() {
        return paths;
    }

    public DefaultItemModelProperties setPaths(String[] paths) {
        this.paths = paths;
        return this;
    }

}
