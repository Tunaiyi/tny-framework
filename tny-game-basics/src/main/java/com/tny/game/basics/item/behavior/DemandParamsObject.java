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

package com.tny.game.basics.item.behavior;

import com.google.common.collect.ImmutableMap;
import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.log.*;
import com.tny.game.common.collection.empty.*;
import com.tny.game.expr.*;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Kun Yang on 2018/5/31.
 */
public class DemandParamsObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

    /**
     * 参数
     */
    protected Map<DemandParam, ExprHolder> paramMap = new EmptyImmutableMap<>();

    /**
     * 事物总管理器
     */
    protected ItemModelContext context;

    public void setAttrMap(long playerId, Collection<String> aliasList, Map<String, Object> attributeMap) {
        for (String alias : aliasList)
            setAttrMap(playerId, alias, attributeMap);
    }

    private void setAttrMap(long playerId, String alias, Map<String, Object> attributeMap) {
        ModelExplorer itemModelExplorer = this.context.getItemModelExplorer();
        ItemModel model = itemModelExplorer.getModelByAlias(alias);
        if (model == null) {
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        }
        ItemExplorer itemExplorer = this.context.getItemExplorer();
        if (itemExplorer.hasItemManager(model.getItemType())) {
            Item<?> item = itemExplorer.getItem(playerId, model.getId());
            attributeMap.put(alias, item);
        }
    }

    public Map<DemandParam, Object> countAndSetDemandParams(String paramsKey, Map<String, Object> attributeMap) {
        if (this.paramMap == null || this.paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<DemandParam, Object> paramMap = new HashMap<>();
        for (Entry<DemandParam, ExprHolder> entry : this.paramMap.entrySet()) {
            try {
                Object value = entry.getValue().createExpr().putAll(attributeMap).execute(Object.class);
                paramMap.put(entry.getKey(), value);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        attributeMap.put(paramsKey, paramMap);
        return paramMap;
    }

    protected void init(ItemModelContext context) {
        this.context = context;
        if (this.paramMap == null) {
            this.paramMap = ImmutableMap.of();
        } else {
            this.paramMap = ImmutableMap.copyOf(this.paramMap);
        }
    }

}
