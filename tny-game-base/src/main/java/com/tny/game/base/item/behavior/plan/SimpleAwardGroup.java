package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.AbstractAwardGroup;
import com.tny.game.base.item.behavior.Award;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽象奖励物品组
 *
 * @author KGTny
 */
public class SimpleAwardGroup extends AbstractAwardGroup {

    @Override
    public List<TradeItem<ItemModel>> countAwardNumber(Map<String, Object> attributeMap) {
        List<TradeItem<ItemModel>> itemList = new ArrayList<>();
        List<Award> awardList = this.randomer.random(this, attributeMap);
        int drawNumber = getDrawNumber(awardList.size(), attributeMap);
        for (Award award : awardList) {
            if (drawNumber <= 0)
                continue;
            String awModelAlias = award.getItemAlias(attributeMap);
            ItemModel awardModel = this.itemModelExplorer.getModelByAlias(awModelAlias);
            if (awardModel == null)
                continue;
            TradeItem<ItemModel> tradeItem = award.createTradeItem(drawNumber > 0, awardModel, attributeMap);
            if (tradeItem != null) {
                drawNumber--;
                itemList.add(tradeItem);
            }
        }
        return itemList;
    }
}