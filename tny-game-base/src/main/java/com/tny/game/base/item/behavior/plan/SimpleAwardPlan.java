package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.AbstractAwardGroup;
import com.tny.game.base.item.behavior.AbstractAwardPlan;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.AwardDetail;
import com.tny.game.base.item.behavior.AwardGroup;
import com.tny.game.base.item.behavior.AwardList;
import com.tny.game.base.item.behavior.AwardPlan;
import com.tny.game.base.item.behavior.DemandHolderObject;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleAwardList;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.probability.DefaultRandomCreatorFactory;
import com.tny.game.base.item.probability.RandomCreator;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 抽象奖励方案
 *
 * @author KGTny
 */
public class SimpleAwardPlan extends AbstractAwardPlan {

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
    protected FormulaHolder number = MvelFormulaFactory.create("1", FormulaType.EXPRESSION);

    /*
     * 产生奖励总类最大个数 -1为无限 默认值为 -1
     */
    protected FormulaHolder drawNumber = MvelFormulaFactory.create("-1", FormulaType.EXPRESSION);

    /**
     * 奖励方式
     */
    //	protected AlertType alertType;

    /**
     *
     */
    protected List<AwardGroup> awardGroupSet;

    /**
     * 附加参数
     */
    protected Set<String> attrAliasSet;

    protected ItemExplorer itemExplorer;

    protected ModelExplorer itemModelExplorer;

    public SimpleAwardPlan(RandomCreator<AwardPlan, AwardGroup> randomer, TreeSet<AwardGroup> treeSet) {
        this.randomer = randomer;
        this.awardGroupSet = new ArrayList<>(treeSet);
    }

    public int getDrawNumber(int awardNum, Map<String, Object> attributeMap) {
        if (this.drawNumber == null)
            return awardNum;
        Integer drawNumber = this.drawNumber.createFormula().putAll(attributeMap).execute(Integer.class);
        if (drawNumber == null)
            drawNumber = -1;
        return drawNumber <= 0 ? awardNum : Math.min(drawNumber, awardNum);
    }

    @Override
    public int getNumber(Map<String, Object> attributeMap) {
        Integer awardNum = this.number.createFormula().putAll(attributeMap).execute(Integer.class);
        if (awardNum == null)
            awardNum = 1;
        return awardNum;
    }

    @Override
    public Trade createTrade(long playerID, Action action, Map<String, Object> attributeMap) {
        List<AwardGroup> groupList = this.randomer.random(this, attributeMap);
        if (groupList == null || groupList.isEmpty())
            return new SimpleTrade(action, TradeType.AWARD);
        // int drawNumber = getDrawNumber(groupList.size(), attributeMap);
        List<TradeItem<ItemModel>> tradeItems = new ArrayList<>();
        for (AwardGroup group : groupList) {
            // if (drawNumber <= 0)
            //     break;
            // drawNumber--;
            List<TradeItem<ItemModel>> award = group.countAwardResult(playerID, action, this.merge, attributeMap);
            if (award != null) {
                tradeItems.addAll(award);
            }
        }
        SimpleTrade trade = new SimpleTrade(action, TradeType.AWARD, tradeItems);
        if (merge)
            return trade.merge();
        return trade;
    }

    @Override
    public AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap) {
        DemandHolderObject.setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        List<AwardDetail> resultList = new ArrayList<>();
        for (AwardGroup group : this.awardGroupSet) {
            AwardDetail detail = new AwardDetail(group.countAwardNumber(this.merge, attributeMap));
            resultList.add(detail);
        }
        return new SimpleAwardList(action, resultList);
    }


    @Override
    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        if (this.randomer == null)
            this.randomer = DefaultRandomCreatorFactory.getInstance();
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.awardGroupSet == null)
            this.awardGroupSet = new ArrayList<>();
        for (AwardGroup awardGroup : this.awardGroupSet) {
            if (awardGroup instanceof AbstractAwardGroup)
                ((AbstractAwardGroup) awardGroup).init(itemExplorer, itemModelExplorer);
        }
        if (this.number == null)
            this.number = MvelFormulaFactory.create("1", FormulaType.EXPRESSION);
        if (this.drawNumber == null)
            this.drawNumber = MvelFormulaFactory.create("-1", FormulaType.EXPRESSION);
        //        Collections.sort(this.awardGroupSet);
        this.awardGroupSet = Collections.unmodifiableList(this.awardGroupSet);
        if (this.attrAliasSet == null)
            this.attrAliasSet = new HashSet<>(0);
        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
    }

    @Override
    public List<AwardGroup> probabilities() {
        return awardGroupSet;
    }

    @Override
    public int getRange(Map<String, Object> attributeMap) {
        return range;
    }

}
