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

package com.tny.game.basics.item.loader.jackson.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.item.loader.jackson.*;
import com.tny.game.expr.*;

/**
 * 模型加载器
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:38
 **/
public class YamlModelLoader<M extends Model> extends JacksonModelLoader<M> {

    public YamlModelLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler, ExprHolderFactory exprHolderFactory) {
        super(modelClass, loadHandler, exprHolderFactory, new YAMLFactory());
    }

    @Override
    protected String getFileType() {
        return "yaml";
    }

}
