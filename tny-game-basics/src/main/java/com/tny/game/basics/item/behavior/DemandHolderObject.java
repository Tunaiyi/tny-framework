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

import com.google.common.collect.*;
import com.tny.game.basics.item.*;

import java.util.*;

public abstract class DemandHolderObject extends DemandParamsObject {

    /**
     * 操作条件
     */
    protected List<BaseDemand> demandList;

    /**
     * 附加参数
     */
    protected Set<String> attrAliasSet;

    protected DemandResultCollector checkResult(long playerId, List<BaseDemand> demandList, boolean tryAll,
            DemandResultCollector collector, String paramsKey, Map<String, Object> attributeMap) {
        setAttrMap(playerId, this.getAttributesAliasSet(), attributeMap);
        this.countAndSetDemandParams(paramsKey, attributeMap);
        if (collector == null) {
            collector = new DemandResultCollector();
        }
        for (Demand demand : demandList) {
            DemandResult result = demand.checkDemandResult(playerId, attributeMap);
            if (result != null) {
                collector.addDemandResult(result);
                if (!tryAll && collector.isFailed()) {
                    return collector;
                }
            }
        }
        return collector;
    }

    public Set<String> getAttributesAliasSet() {
        return this.attrAliasSet;
    }

    protected List<DemandResult> countAllDemandResults(long playerId, List<BaseDemand> demandList,
            String paramsKey, Map<String, Object> attributeMap) {
        setAttrMap(playerId, this.getAttributesAliasSet(), attributeMap);
        this.countAndSetDemandParams(paramsKey, attributeMap);
        List<DemandResult> demandResults = new ArrayList<>();
        for (Demand demand : demandList) {
            DemandResult result = demand.checkDemandResult(playerId, attributeMap);
            if (result != null) {
                demandResults.add(result);
            }
        }
        return demandResults;
    }

    public void init(ItemModel itemModel, ItemModelContext context) {
        this.init(context);
        if (this.demandList == null) {
            this.demandList = ImmutableList.of();
        }
        if (this.attrAliasSet == null) {
            this.attrAliasSet = ImmutableSet.of();
        }
        for (BaseDemand demand : this.demandList) {
            demand.init(itemModel, this.context);
        }
        this.demandList = Collections.unmodifiableList(this.demandList);
        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
    }

}
