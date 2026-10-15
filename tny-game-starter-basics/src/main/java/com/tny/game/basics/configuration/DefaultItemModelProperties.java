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
package com.tny.game.basics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.basics.configuration.BasicsPropertyConstants.*;
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
