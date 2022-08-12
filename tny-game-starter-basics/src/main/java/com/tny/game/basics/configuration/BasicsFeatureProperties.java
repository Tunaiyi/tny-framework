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

import com.tny.game.basics.develop.*;
import com.tny.game.basics.item.mould.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;

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
