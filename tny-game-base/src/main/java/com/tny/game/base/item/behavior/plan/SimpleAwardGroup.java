package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.AbstractAwardGroup;
import com.tny.game.base.item.behavior.Award;
import com.tny.game.base.item.behavior.trade.CollectionTradeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象奖励物品组
 *
 * @author KGTny
 */
public class SimpleAwardGroup extends AbstractAwardGroup {

    @Override
    public List<TradeItem<ItemModel>> countAwardNumber(boolean merge, Map<String, Object> attributeMap) {
        List<TradeItem<ItemModel>> itemList = new ArrayList<>();
        List<Award> awardList = this.randomer.random(this, attributeMap);
        int drawNumber = getDrawNumber(awardList.size(), attributeMap);
        Map<Integer, CollectionTradeItem> itemMap = null;
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