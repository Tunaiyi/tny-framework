package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.base.item.behavior.simple.SimpleAwardList;
import com.tny.game.base.item.behavior.simple.SimpleTrade;

import java.util.*;

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
     * 随机器
     */
    protected RandomCreator<AwardGroup> randomer;

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

    protected ItemModelExplorer itemModelExplorer;

    public SimpleAwardPlan(RandomCreator<AwardGroup> randomer, TreeSet<AwardGroup> treeSet) {
        this.randomer = randomer;
        this.awardGroupSet = new ArrayList<AwardGroup>(treeSet);
    }

    @Override
    public Trade createTrade(long playerID, Action action, Map<String, Object> attributeMap) {
        List<AwardGroup> groupList = this.randomer.random(this.range, 1, this.awardGroupSet, attributeMap);
        if (groupList == null || groupList.isEmpty())
            return new SimpleTrade(action, TradeType.AWARD);
        AwardGroup group = groupList.get(0);
        return group.countAwardResult(playerID, action, attributeMap);
    }

    @Override
    public AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap) {
        DemandHolderObject.setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        List<AwardDetail> resultList = new ArrayList<AwardDetail>();
        for (AwardGroup group : this.awardGroupSet) {
            AwardDetail detail = new AwardDetail(group.getProbability(), group.countAwardNumber(attributeMap));
            resultList.add(detail);
        }
        return new SimpleAwardList(action, resultList);
    }


    @Override
    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ItemModelExplorer itemModelExplorer) {
        if (this.randomer == null)
            this.randomer = DefaultRandomCreatorFactory.getInstance();
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.awardGroupSet == null)
            this.awardGroupSet = new ArrayList<AwardGroup>();
        for (AwardGroup awardGroup : this.awardGroupSet) {
            if (awardGroup instanceof AbstractAwardGroup)
                ((AbstractAwardGroup) awardGroup).init(itemExplorer, itemModelExplorer);
        }
        Collections.sort(this.awardGroupSet);
        this.awardGroupSet = Collections.unmodifiableList(this.awardGroupSet);

        if (this.attrAliasSet == null)
            this.attrAliasSet = new HashSet<String>(0);
        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
    }

}
