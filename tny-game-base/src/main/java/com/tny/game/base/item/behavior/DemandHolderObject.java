package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ModelExplorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DemandHolderObject {

    /**
     * 操作条件
     */
    protected List<AbstractDemand> demandList;

    /**
     * 附加参数
     */
    protected Set<String> attrAliasSet;

    /**
     * 事物总管理器
     */
    protected ItemExplorer itemExplorer;

    /**
     * 事物模型总管理器
     */
    protected ModelExplorer itemModelExplorer;

    public static void setAttrMap(long playerID, Collection<String> aliasList, ModelExplorer itemModelExplorer, ItemExplorer itemExplorer, Map<String, Object> attributeMap) {
        for (String alias : aliasList)
            setAttrMap(playerID, alias, itemModelExplorer, itemExplorer, attributeMap);
    }

    public static Item<?> setAttrMap(long playerID, String alias, ModelExplorer itemModelExplorer, ItemExplorer itemExplorer, Map<String, Object> attributeMap) {
        ItemModel model = itemModelExplorer.getModelByAlias(alias);
        if (model == null)
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        Item<?> item = null;
        if (model.getItemType().hasEntity()) {
            item = itemExplorer.getItem(playerID, model.getID());
            attributeMap.put(alias, item);
        }
        return item;
    }

    protected List<DemandResult> checkResult(long playerID, List<AbstractDemand> demandList, boolean tryAll, Map<String, Object> attributeMap) {
        setAttrMap(playerID, this.getAttributesAliasSet(), this.itemModelExplorer, this.itemExplorer, attributeMap);
        List<DemandResult> results = new ArrayList<>();
        for (Demand demand : demandList) {
            DemandResult result = demand.checkDemandResult(playerID, attributeMap);
            if (result != null && !result.isSatisfy()) {
                results.add(result);
                if (!tryAll)
                    return results;
            }
        }
        return results;
    }

    public Set<String> getAttributesAliasSet() {
        return this.attrAliasSet;
    }

    protected List<DemandResult> countDemandResultList(long playerID, List<AbstractDemand> demandList,
                                                       Map<String, Object> attributeMap) {
        setAttrMap(playerID, this.getAttributesAliasSet(), this.itemModelExplorer, this.itemExplorer, attributeMap);
        List<DemandResult> demandResults = new ArrayList<>();
        for (Demand demand : demandList) {
            DemandResult result = demand.checkDemandResult(playerID, attributeMap);
            if (result != null)
                demandResults.add(result);
        }
        return demandResults;
    }

    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.demandList == null)
            this.demandList = new ArrayList<>();
        if (this.attrAliasSet == null)
            this.attrAliasSet = new HashSet<>(0);

        for (AbstractDemand demand : this.demandList) {
            demand.init(itemModel, itemExplorer, itemModelExplorer);
        }
        this.demandList = Collections.unmodifiableList(this.demandList);
        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
    }

}
