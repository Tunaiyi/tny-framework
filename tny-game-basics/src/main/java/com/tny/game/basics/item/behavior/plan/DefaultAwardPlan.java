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

package com.tny.game.basics.item.behavior.plan;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.simple.*;
import com.tny.game.basics.item.probability.*;
import com.tny.game.expr.*;

import java.util.*;

/**
 * 抽象奖励方案
 *
 * @author KGTny
 */
public class DefaultAwardPlan extends BaseAwardPlan {

    /**
     * 随机范围
     */
    protected int range;

    /**
     * 数量合并
     */
    protected boolean merge = false;

    /**
     * 随机器
     */
    protected RandomCreator<AwardPlan, AwardGroup> randomer;

    /*
     * 产生奖励总类最大个数 -1为无限 默认值为 -1
     */
    protected ExprHolder number;

    /**
     *
     */
    protected List<AwardGroup> awardGroupList;

    /**
     * 附加参数
     */
    protected Set<String> attrAliasSet;

    protected ItemModelContext context;

    public DefaultAwardPlan() {
    }

    public DefaultAwardPlan(RandomCreator<AwardPlan, AwardGroup> randomer, TreeSet<AwardGroup> treeSet) {
        this.randomer = randomer;
        this.awardGroupList = new ArrayList<>(treeSet);
    }

    // public int getDrawNumber(int awardNum, Map<String, Object> attributeMap) {
    //     if (this.drawNumber == null)
    //         return awardNum;
    //     Integer drawNumber = this.drawNumber.createFormula().putAll(attributeMap).execute(Integer.class);
    //     if (drawNumber == null)
    //         drawNumber = -1;
    //     return drawNumber <= 0 ? awardNum : Math.min(drawNumber, awardNum);
    // }
    //
    @Override
    public int getNumber(Map<String, Object> attributeMap) {
        Integer awardNum = this.number.createExpr().putAll(attributeMap).execute(Integer.class);
        if (awardNum == null) {
            awardNum = 1;
        }
        return awardNum;
    }

    @Override
    public Trade createTrade(long playerId, Action action, Map<String, Object> attributeMap) {
        countAndSetDemandParams(ItemsImportKey.$AWARD_PLAN_DEMAND_PARAMS, attributeMap);
        List<AwardGroup> groupList = this.randomer.random(this, attributeMap);
        if (groupList == null || groupList.isEmpty()) {
            return new SimpleTrade(action, TradeType.AWARD);
        }
        // int drawNumber = getDrawNumber(groupList.size(), attributeMap);
        List<TradeItem<StuffModel>> tradeItems = new ArrayList<>();
        for (AwardGroup group : groupList) {
            // if (drawNumber <= 0)
            //     break;
            // drawNumber--;
            List<TradeItem<StuffModel>> award = group.countAwardResult(playerId, action, this.merge, attributeMap);
            if (award != null) {
                tradeItems.addAll(award);
            }
        }
        SimpleTrade trade = new SimpleTrade(action, TradeType.AWARD, tradeItems);
        if (this.merge) {
            return trade.merge();
        }
        return trade;
    }

    @Override
    public AwardList getAwardList(long playerId, Action action, Map<String, Object> attributeMap) {
        countAndSetDemandParams(ItemsImportKey.$AWARD_PLAN_DEMAND_PARAMS, attributeMap);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        List<AwardDetail> resultList = new ArrayList<>();
        for (AwardGroup group : this.awardGroupList) {
            AwardDetail detail = new AwardDetail(group.countAwardNumber(this.merge, attributeMap));
            resultList.add(detail);
        }
        return new SimpleAwardList(action, resultList);
    }

    @Override
    public void init(ItemModel itemModel, ItemModelContext context) {
        this.init(context);
        if (this.randomer == null) {
            this.randomer = SequenceRandomCreatorFactory.<AwardPlan, AwardGroup>getInstance().getRandomCreator();
        }
        if (this.awardGroupList == null) {
            this.awardGroupList = Collections.emptyList();
        }
        for (AwardGroup awardGroup : this.awardGroupList) {
            if (awardGroup instanceof BaseAwardGroup) {
                ((BaseAwardGroup) awardGroup).init(context);
            }
        }
        ExprHolderFactory exprHolderFactory = context.getExprHolderFactory();
        if (this.number == null) {
            this.number = exprHolderFactory.create("1");
        }
        // if (this.drawNumber == null)
        //     this.drawNumber = MvelFormulaFactory.create("-1", FormulaType.EXPRESSION);
        //        Collections.sort(this.awardGroupSet);
        if (this.attrAliasSet == null) {
            this.attrAliasSet = Collections.emptySet();
        }
        this.awardGroupList = Collections.unmodifiableList(this.awardGroupList);
        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
    }

    @Override
    public List<AwardGroup> probabilities() {
        return this.awardGroupList;
    }

    @Override
    public int getRange(Map<String, Object> attributeMap) {
        return this.range;
    }

}
