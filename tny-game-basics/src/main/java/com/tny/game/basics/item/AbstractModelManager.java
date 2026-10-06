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

package com.tny.game.basics.item;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.log.*;
import com.tny.game.common.concurrent.collection.*;
import org.slf4j.*;

import java.util.*;

/**
 * 抽象事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public abstract class AbstractModelManager<M extends Model> implements ModelManager<M> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

    /**
     * 管理的事物模型
     */
    protected Map<Integer, M> modelMap = new CopyOnWriteMap<>();

    /**
     * 管理的事物模型
     */
    protected Map<String, M> modelAliasMap = new CopyOnWriteMap<>();

    @Override
    public M getModel(int id) {
        M model = this.modelMap.get(id);
        if (model == null) {
            LOGGER.warn("", new GameRuningException(id, ItemResultCode.MODEL_NO_EXIST, id));
        }
        return model;
    }

    @Override
    public M getModelByAlias(String itemAlias) {
        M model = this.modelAliasMap.get(itemAlias);
        if (model == null) {
            LOGGER.warn("", new GameRuningException(itemAlias, ItemResultCode.MODEL_NO_EXIST, itemAlias));
        }
        return model;
    }

    @Override
    public M getAndCheckModel(int id) {
        M model = this.getModel(id);
        if (model == null) {
            throw new GameRuningException(id, ItemResultCode.MODEL_NO_EXIST, id);
        }
        return model;
    }

    @Override
    public M getAndCheckModelByAlias(String itemAlias) {
        M model = this.getModelByAlias(itemAlias);
        if (model == null) {
            throw new GameRuningException(itemAlias, ItemResultCode.MODEL_NO_EXIST, itemAlias);
        }
        return model;
    }

    @Override
    public Map<Integer, M> getModelMap(Collection<Integer> idCollection) {
        Map<Integer, M> modelMap = new HashMap<>(idCollection.size());
        for (Integer id : idCollection)
            modelMap.put(id, this.getModel(id));
        return modelMap;
    }

    @Override
    public Map<Integer, M> getAllModelMap() {
        return Collections.unmodifiableMap(this.modelMap);
    }

    @Override
    public Collection<M> getModelCollection(Collection<Integer> idCollection) {
        List<M> modelList = new ArrayList<>(idCollection.size());
        for (Integer id : idCollection)
            modelList.add(this.getModel(id));
        return modelList;
    }

    @Override
    public Collection<M> getAllModel() {
        return Collections.unmodifiableCollection(this.modelMap.values());
    }

}
