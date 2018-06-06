package com.tny.game.base.item.behavior;

import com.google.common.collect.ImmutableMap;
import com.tny.game.base.exception.*;
import com.tny.game.base.item.*;
import com.tny.game.base.log.LogName;
import com.tny.game.expr.ExprHolder;
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
    protected Map<DemandParam, ExprHolder> paramMap;

    /**
     * 事物总管理器
     */
    protected ItemModelContext context;

    public void setAttrMap(long playerID, Collection<String> aliasList, Map<String, Object> attributeMap) {
        for (String alias : aliasList)
            setAttrMap(playerID, alias, attributeMap);
    }

    private void setAttrMap(long playerID, String alias, Map<String, Object> attributeMap) {
        ModelExplorer itemModelExplorer = context.getItemModelExplorer();
        ItemModel model = itemModelExplorer.getModelByAlias(alias);
        if (model == null)
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        ItemExplorer itemExplorer = context.getItemExplorer();
        if (itemExplorer.hasItemManager(model.getItemType())) {
            Item<?> item = itemExplorer.getItem(playerID, model.getID());
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
        if (paramMap == null)
            paramMap = ImmutableMap.of();
        else
            paramMap = ImmutableMap.copyOf(paramMap);
    }

}
