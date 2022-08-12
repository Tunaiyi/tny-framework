/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
