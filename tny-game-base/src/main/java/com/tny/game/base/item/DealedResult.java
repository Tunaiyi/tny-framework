package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;

import java.util.List;

/**
 * 交易该改变对象
 *
 * @author KGTny
 */
public interface DealedResult {

    /**
     * 交易相关Action
     *
     * @return
     */
    public Action getAction();

    /**
     * 获取完成交易的对象
     *
     * @return
     */
    public List<DealedItem<?>> getDealedItemList();

}
