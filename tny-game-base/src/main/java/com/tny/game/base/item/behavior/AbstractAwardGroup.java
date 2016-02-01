package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;

import java.util.*;

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
    protected int probability;

    /**
     * 优先级 大值优先
     */
    private int priority = 0;

    /*
     * 产生奖励总类最大个数 -1为无限 默认值为 -1
     */
    private FormulaHolder number = MvelFormulaFactory.create("1", FormulaType.EXPRESSION);

    /**
     * 条件 group生效条件
     */
    protected FormulaHolder condition;

    /**
     * 随机器
     */
    protected RandomCreator<Award> randomer = AllRandomCreatorFactory.getInstance();

    /**
     * 奖励列表
     */
    protected List<Award> awardList;

    /**
     * item模型浏览器
     */
    protected ItemModelExplorer itemModelExplorer;

    @Override
    public int getProbability() {
        return this.probability;
    }

    @Override
    public int compareTo(Probability o) {
        int value = o.getPriority() - this.priority;
        if (value == 0)
            return this.getProbability() - o.getProbability();
        return value;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public Set<ItemModel> getAwardSet(Map<String, Object> attributeMap) {
        return new HashSet<ItemModel>(this.getAwardAliasModelMap(attributeMap).values());
    }

    @Override
    public List<TradeItem<ItemModel>> countAwardNumber(Map<String, Object> attributeMap) {
        List<TradeItem<ItemModel>> itemList = new ArrayList<TradeItem<ItemModel>>();
        Integer awardNum = this.number.createFormula().putAll(attributeMap).execute(Integer.class);
        if (awardNum == null)
            awardNum = 1;
        List<Award> awardList = this.randomer.random(10000, awardNum, this.awardList, attributeMap);
        for (Award award : awardList) {
            String awModelAlias = award.getItemAlias(attributeMap);
            ItemModel awardModel = this.itemModelExplorer.getItemModelByAlias(awModelAlias);
            if (awardModel == null)
                continue;
            AlterType type = award.getAlterType();
            Map<DemandParam, Object> paramMap = award.countDemandParam(attributeMap);
            int number = award.countNumber(awardModel, attributeMap);
            if (number > 0) {
                itemList.add(new SimpleTradeItem<ItemModel>(awardModel, type == null ? AlterType.IGNORE : type, number, paramMap));
            }
        }
        return itemList;
    }

    @Override
    public Trade countAwardResult(long playerID, Action action, Map<String, Object> attributeMap) {
        List<TradeItem<ItemModel>> tradeItemList = this.countAwardNumber(attributeMap);
        Trade trade = new SimpleTrade(action, TradeType.AWARD, tradeItemList);
        //		CreateAwardEvent.dispartch(this, playerID, action, tradeItemList);
        return trade;
    }

    private Map<String, ItemModel> getAwardAliasModelMap(Map<String, Object> attributeMap) {
        Map<String, ItemModel> map = new HashMap<String, ItemModel>();
        for (Award award : this.awardList) {
            ItemModel model = this.itemModelExplorer.getItemModelByAlias(award.getItemAlias(attributeMap));
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

    //	public AwardCreateType getAwardWay() {
    //		return createType;
    //	}

    public void init(ItemExplorer itemExplorer, ItemModelExplorer itemModelExplorer) {
        this.itemModelExplorer = itemModelExplorer;
        if (this.randomer == null)
            this.randomer = AllRandomCreatorFactory.getInstance();
        for (Award award : this.awardList) {
            if (award instanceof AbstractAward)
                ((AbstractAward) award).init();
        }
        if (this.number == null)
            this.number = MvelFormulaFactory.create("1", FormulaType.EXPRESSION);
        //		if (this.createType == null)
        //			this.createType = AwardCreateType.ABSOLUTE;
        Collections.sort(this.awardList);
        this.awardList = Collections.unmodifiableList(this.awardList);
    }

}
