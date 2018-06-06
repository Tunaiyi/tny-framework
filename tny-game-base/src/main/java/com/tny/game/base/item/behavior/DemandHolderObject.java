package com.tny.game.base.item.behavior;

import com.google.common.collect.*;
import com.tny.game.base.item.*;

import java.util.*;

public abstract class DemandHolderObject extends DemandParamsObject {

    /**
     * 操作条件
     */
    protected List<AbstractDemand> demandList;

    /**
     * 附加参数
     */
    protected Set<String> attrAliasSet;


    protected DemandResultCollector checkResult(long playerID, List<AbstractDemand> demandList, boolean tryAll,
                                                DemandResultCollector collector, String paramsKey, Map<String, Object> attributeMap) {
        setAttrMap(playerID, this.getAttributesAliasSet(), attributeMap);
        this.countAndSetDemandParams(paramsKey, attributeMap);
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
                                                       String paramsKey, Map<String, Object> attributeMap) {
        setAttrMap(playerID, this.getAttributesAliasSet(), attributeMap);
        this.countAndSetDemandParams(paramsKey, attributeMap);
        List<DemandResult> demandResults = new ArrayList<>();
        for (Demand demand : demandList) {
            DemandResult result = demand.checkDemandResult(playerID, attributeMap);
            if (result != null)
                demandResults.add(result);
        }
        return demandResults;
    }

    public void init(ItemModel itemModel, ItemModelContext context) {
        this.init(context);
        if (this.demandList == null)
            this.demandList = ImmutableList.of();
        if (this.attrAliasSet == null)
            this.attrAliasSet = ImmutableSet.of();
        for (AbstractDemand demand : this.demandList) {
            demand.init(itemModel, this.context);
        }
        this.demandList = Collections.unmodifiableList(this.demandList);
        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
    }

}
