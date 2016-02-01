package com.tny.game.base.item.behavior;

import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Probability;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 奖励物品组
 *
 * @author KGTny
 */
public interface AwardGroup extends Probability {

    /**
     * 获取奖励组所奖励的所有物品ID列表
     *
     * @return
     */
    public Set<ItemModel> getAwardSet(Map<String, Object> attributeMap);

    /**
     * 计算该组奖励物品和数量
     *
     * @param attributeMap 计算参数
     * @return 返回奖励的物品和数量
     */
    public List<TradeItem<ItemModel>> countAwardNumber(Map<String, Object> attributeMap);

    /**
     * 创建奖励结果集
     *
     * @param action       奖励的操作类型
     * @param alertType    奖励的方式
     * @param attributeMap 附加参数
     * @return 返回结果集
     */
    public Trade countAwardResult(long playerID, Action action, Map<String, Object> attributeMap);

    /**
     * 是否生效
     *
     * @param attributeMap
     * @return
     */
    public boolean isEffect(Map<String, Object> attributeMap);

}
