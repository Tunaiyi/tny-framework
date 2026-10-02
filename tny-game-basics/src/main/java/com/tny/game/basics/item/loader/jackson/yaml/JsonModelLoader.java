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

package com.tny.game.basics.item.loader.jackson.yaml;

import com.fasterxml.jackson.core.JsonFactory;
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
public class JsonModelLoader<M extends Model> extends JacksonModelLoader<M> {

    public JsonModelLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler, ExprHolderFactory exprHolderFactory) {
        super(modelClass, loadHandler, exprHolderFactory, new JsonFactory());
    }

    @Override
    protected String getFileType() {
        return "json";
    }

}
