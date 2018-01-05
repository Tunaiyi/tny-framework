package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.*;
import com.tny.game.base.item.*;

import java.util.*;

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

    private static void setAttrMap(long playerID, String alias, ModelExplorer itemModelExplorer, ItemExplorer itemExplorer, Map<String, Object> attributeMap) {
        ItemModel model = itemModelExplorer.getModelByAlias(alias);
        if (model == null)
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        if (itemExplorer.hasItemMannager(model.getItemType())) {
            Item<?> item = itemExplorer.getItem(playerID, model.getID());
            attributeMap.put(alias, item);
        }
    }

    protected DemandResultCollector checkResult(long playerID, List<AbstractDemand> demandList, boolean tryAll, DemandResultCollector collector, Map<String, Object> attributeMap) {
        setAttrMap(playerID, this.getAttributesAliasSet(), this.itemModelExplorer, this.itemExplorer, attributeMap);
        if (collector == null)
            collector = new DemandResultCollector();
        for (Demand demand : demandList) {
            DemandResult result = demand.checkDemandResult(playerID, attributeMap);
            if (result != null) {
                collector.addDemandResult(result);
                if (!tryAll && collector.isFailed())
                    return collector;
            }
        }
        return collector;
    }

    public Set<String> getAttributesAliasSet() {
        return this.attrAliasSet;
    }

    protected List<DemandResult> countAllDemandResults(long playerID, List<AbstractDemand> demandList,
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
