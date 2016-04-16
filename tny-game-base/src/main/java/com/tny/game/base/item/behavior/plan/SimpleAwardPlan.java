package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.DefaultRandomCreatorFactory;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemModelExplorer;
import com.tny.game.base.item.RandomCreator;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.behavior.AbstractAwardGroup;
import com.tny.game.base.item.behavior.AbstractAwardPlan;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.AwardDetail;
import com.tny.game.base.item.behavior.AwardGroup;
import com.tny.game.base.item.behavior.AwardList;
import com.tny.game.base.item.behavior.DemandHolderObject;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleAwardList;
import com.tny.game.base.item.behavior.simple.SimpleTrade;

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
        this.awardGroupSet = new ArrayList<>(treeSet);
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
        List<AwardDetail> resultList = new ArrayList<>();
        for (AwardGroup group : this.awardGroupSet) {
            AwardDetail detail = new AwardDetail(group.countAwardNumber(attributeMap));
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
            this.awardGroupSet = new ArrayList<>();
        for (AwardGroup awardGroup : this.awardGroupSet) {
            if (awardGroup instanceof AbstractAwardGroup)
                ((AbstractAwardGroup) awardGroup).init(itemExplorer, itemModelExplorer);
        }
        //        Collections.sort(this.awardGroupSet);
        this.awardGroupSet = Collections.unmodifiableList(this.awardGroupSet);

        if (this.attrAliasSet == null)
            this.attrAliasSet = new HashSet<>(0);
        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
    }

}
