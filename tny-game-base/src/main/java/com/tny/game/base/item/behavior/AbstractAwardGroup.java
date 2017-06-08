package com.tny.game.base.item.behavior;

import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.trade.CollectionTradeItem;
import com.tny.game.base.item.probability.AllRandomCreatorFactory;
import com.tny.game.base.item.probability.RandomCreator;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 抽象奖励物品组
 *
 * @author KGTny
 */
public abstract class AbstractAwardGroup implements AwardGroup {

    //	/**
    //	 * 奖励创建方式 默认_ABSOLUTE
    //	 */
    //	public AwardCreateType createType = AwardCreateType.ABSOLUTE;

    /**
     * 获得该奖励组的概率参数
     */
    protected FormulaHolder probability;

//    /**
//     * 优先级 大值优先
//     */
//    private int priority = 0;

    /*
     * 产生奖励总类最大个数 -1为无限 默认值为 -1
     */
    protected FormulaHolder number = MvelFormulaFactory.create("1", FormulaType.EXPRESSION);

    /*
     * 产生奖励总类最大个数 -1为无限 默认值为 -1
     */
    protected FormulaHolder drawNumber = MvelFormulaFactory.create("-1", FormulaType.EXPRESSION);

    /**
     * 条件 group生效条件
     */
    protected FormulaHolder condition;

    /**
     * 随机器
     */
    protected RandomCreator<AwardGroup, Award> randomer = AllRandomCreatorFactory.getInstance();

    /**
     * 奖励列表
     */
    protected List<Award> awardList;

    /**
     * item模型浏览器
     */
    protected ModelExplorer itemModelExplorer;

    @Override
    public int getProbability(Map<String, Object> attributeMap) {
        return this.probability.createFormula()
                .putAll(attributeMap)
                .execute(Integer.class);
    }


    @Override
    public List<Award> probabilities() {
        return this.awardList;
    }

//    @Override
//    public int compareTo(Probability o) {
//        int value = o.getPriority() - this.priority;
//        if (value == 0)
//            return this.getProbability() - o.getProbability();
//        return value;
//    }

//    @Override
//    public int getPriority() {
//        return this.priority;
//    }

    @Override
    public Set<ItemModel> getAwardSet(Map<String, Object> attributeMap) {
        return new HashSet<>(this.getAwardAliasModelMap(attributeMap).values());
    }

    @Override
    public List<TradeItem<ItemModel>> countAwardResult(long playerID, Action action, boolean merge, Map<String, Object> attributeMap) {
        return this.countAwardNumber(merge, attributeMap);
    }

    private Map<String, ItemModel> getAwardAliasModelMap(Map<String, Object> attributeMap) {
        Map<String, ItemModel> map = new HashMap<>();
        for (Award award : this.awardList) {
            ItemModel model = this.itemModelExplorer.getModelByAlias(award.getItemAlias(attributeMap));
            map.put(model.getAlias(), model);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean isEffect(Map<String, Object> attributeMap) {
        return this.condition == null ? true : this.condition.createFormula()
                .putAll(attributeMap)
                .execute(Boolean.class);
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
    public int getRange(Map<String, Object> attributeMap) {
        return 10000;
    }

    public void init(ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        this.itemModelExplorer = itemModelExplorer;
        if (this.randomer == null)
            this.randomer = AllRandomCreatorFactory.getInstance();
        for (Award award : this.awardList) {
            if (award instanceof AbstractAward)
                ((AbstractAward) award).init();
        }
        if (this.number == null)
            this.number = MvelFormulaFactory.create("1", FormulaType.EXPRESSION);
        if (this.drawNumber == null)
            this.drawNumber = MvelFormulaFactory.create("-1", FormulaType.EXPRESSION);
        //		if (this.createType == null)
        //			this.createType = AwardCreateType.ABSOLUTE;
        //        Collections.sort(this.awardList);
        this.awardList = Collections.unmodifiableList(this.awardList);
    }

    @Override
    public List<TradeItem<ItemModel>> countAwardNumber(boolean merge, Map<String, Object> attributeMap) {
        List<TradeItem<ItemModel>> itemList = new ArrayList<>();
        List<Award> awardList = this.randomer.random(this, attributeMap);
        int drawNumber = getDrawNumber(awardList.size(), attributeMap);
        Map<Integer, CollectionTradeItem> itemMap = null;
        for (Award award : awardList) {
            if (drawNumber <= 0)
                break;
            String awModelAlias = award.getItemAlias(attributeMap);
            ItemModel awardModel = this.itemModelExplorer.getModelByAlias(awModelAlias);
            if (awardModel == null)
                continue;
            TradeItem<ItemModel> tradeItem = award.createTradeItem(drawNumber > 0, awardModel, attributeMap);
            if (tradeItem != null) {
                drawNumber--;
                if (merge) {
                    if (itemMap == null)
                        itemMap = new HashMap<>();
                    int itemID = awardModel.getID();
                    CollectionTradeItem current = itemMap.get(itemID);
                    if (current == null) {
                        current = new CollectionTradeItem(tradeItem);
                        itemMap.put(tradeItem.getItemModel().getID(), current);
                        itemList.add(current);
                    } else {
                        current.collect(tradeItem);
                    }
                } else {
                    itemList.add(tradeItem);
                }
            }
        }
        return itemList;
    }

}
