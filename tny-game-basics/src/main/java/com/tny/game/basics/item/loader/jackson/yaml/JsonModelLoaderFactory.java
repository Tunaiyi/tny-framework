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

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.expr.*;

/**
 * 模型加载器工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:39
 **/
public class JsonModelLoaderFactory implements ModelLoaderFactory {

    private final ExprHolderFactory exprHolderFactory;

    public JsonModelLoaderFactory(ExprHolderFactory exprHolderFactory) {
        this.exprHolderFactory = exprHolderFactory;
    }

    @Override
    public <M extends Model> ModelLoader<M> createLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler) {
        return new JsonModelLoader<>(modelClass, loadHandler, exprHolderFactory);
    }

}
