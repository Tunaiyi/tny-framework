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

package com.tny.game.basics.item.model;

import com.tny.game.basics.item.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * xml映射事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public abstract class BaseModelManager<M extends Model> extends AbstractModelManager<M> {

    protected void initModel(Object context, M model) {
        if (model instanceof BaseModel) {
            BaseModel<Object> current = as(model);
            current.init(context);
        }
    }

}
