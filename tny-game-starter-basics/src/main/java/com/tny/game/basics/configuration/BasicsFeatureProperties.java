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

import com.tny.game.basics.develop.*;
import com.tny.game.basics.item.mould.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.basics.configuration.BasicsPropertyConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:23 下午
 */
@ConfigurationProperties(BASICS_FEATURE_MANAGER)
public class BasicsFeatureProperties {

    private boolean enable;

    private String path = ItemModelPaths.FEATURE_MODEL_CONFIG_PATH;

    private Class<? extends DefaultFeatureModel> modelClass = DefaultFeatureModel.class;

    public boolean isEnable() {
        return enable;
    }

    public BasicsFeatureProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getPath() {
        return path;
    }

    public BasicsFeatureProperties setPath(String path) {
        this.path = path;
        return this;
    }

    public Class<? extends DefaultFeatureModel> getModelClass() {
        return modelClass;
    }

    public BasicsFeatureProperties setModelClass(Class<? extends DefaultFeatureModel> modelClass) {
        this.modelClass = modelClass;
        return this;
    }

}
