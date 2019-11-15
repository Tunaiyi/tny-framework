package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.trade.*;
import com.tny.game.base.item.probability.*;
import com.tny.game.expr.*;

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
    protected ExprHolder probability;

    //    /**
    //     * 优先级 大值优先
    //     */
    //    private int priority = 0;

    /*
     * 产生奖励总类最大个数 -1为无限 默认值为 -1
     */
    protected ExprHolder number;

    /*
     * 产生奖励总类最大个数 -1为无限 默认值为 -1
     */
    protected ExprHolder drawNumber;

    /**
     * 条件 group生效条件
     */
    protected ExprHolder condition;

    /**
     * 随机器
     */
    protected RandomCreator<AwardGroup, Award> randomer = AllRandomCreatorFactory.<AwardGroup, Award>getInstance().getRandomCreator();

    /**
     * 奖励列表
     */
    protected List<Award> awardList;

    /**
     * item模型浏览器
     */
    protected ItemModelContext context;

    @Override
    public int getProbability(Map<String, Object> attributeMap) {
        return this.probability.createExpr()
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
        ModelExplorer itemModelExplorer = context.getItemModelExplorer();
        for (Award award : this.awardList) {
            ItemModel model = itemModelExplorer.getModelByAlias(award.getItemAlias(attributeMap));
            map.put(model.getAlias(), model);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean isEffect(Map<String, Object> attributeMap) {
        return this.condition == null ? true : this.condition.createExpr()
                                                             .putAll(attributeMap)
                                                             .execute(Boolean.class);
    }

    public int getDrawNumber(int awardNum, Map<String, Object> attributeMap) {
        if (this.drawNumber == null)
            return awardNum;
        Integer drawNumber = this.drawNumber.createExpr().putAll(attributeMap).execute(Integer.class);
        if (drawNumber == null)
            drawNumber = -1;
        return drawNumber <= 0 ? awardNum : Math.min(drawNumber, awardNum);
    }

    @Override
    public int getNumber(Map<String, Object> attributeMap) {
        Integer awardNum = this.number.createExpr().putAll(attributeMap).execute(Integer.class);
        if (awardNum == null)
            awardNum = 1;
        return awardNum;
    }

    @Override
    public int getRange(Map<String, Object> attributeMap) {
        return 10000;
    }

    @Override
    public List<TradeItem<ItemModel>> countAwardNumber(boolean merge, Map<String, Object> attributeMap) {
        List<TradeItem<ItemModel>> itemList = new ArrayList<>();
        List<Award> awardList = this.randomer.random(this, attributeMap);
        // 需要奖励数量
        int number = awardList.size();
        // 抽中数, 一般number==drawNumber, 如果抽奖类型 number个抽drawNumber个. 并且要显示未抽中的物品,则drawNumber<=number
        int drawNumber = getDrawNumber(number, attributeMap);
        Map<Integer, CollectionTradeItem> itemMap = null;
        ModelExplorer itemModelExplorer = context.getItemModelExplorer();
        for (Award award : awardList) {
            if (number <= 0)
                break;
            String awModelAlias = award.getItemAlias(attributeMap);
            ItemModel awardModel = itemModelExplorer.getModelByAlias(awModelAlias);
            if (awardModel == null)
                continue;
            // drawNumber > 0 => 有效的作为奖励发放, 无效的作为显示, 如抽奖 number个抽drawNumber个
            TradeItem<ItemModel> tradeItem = award.createTradeItem(drawNumber > 0, awardModel, attributeMap);
            if (tradeItem != null) {
                number--;
                drawNumber--;
                if (merge) {
                    if (itemMap == null)
                        itemMap = new HashMap<>();
                    int itemID = awardModel.getId();
                    CollectionTradeItem current = itemMap.get(itemID);
                    if (current == null) {
                        current = new CollectionTradeItem(tradeItem);
                        itemMap.put(tradeItem.getItemModel().getId(), current);
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

    public void init(ItemModelContext context) {
        this.context = context;
        if (this.randomer == null)
            this.randomer = AllRandomCreatorFactory.<AwardGroup, Award>getInstance().getRandomCreator();
        for (Award award : this.awardList) {
            if (award instanceof AbstractAward)
                ((AbstractAward) award).init();
        }
        ExprHolderFactory exprHolderFactory = context.getExprHolderFactory();
        if (this.number == null)
            this.number = exprHolderFactory.create("1");
        if (this.drawNumber == null)
            this.drawNumber = exprHolderFactory.create("-1");
        //		if (this.createType == null)
        //			this.createType = AwardCreateType.ABSOLUTE;
        //        Collections.sort(this.awardList);
        this.awardList = Collections.unmodifiableList(this.awardList);
    }

}
