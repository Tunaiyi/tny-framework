package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;

import java.util.List;

/**
 * 交易该改变对象
 *
 * @author KGTny
 */
public interface DealResult {

    /**
     * 交易相关Action
     *
     * @return
     */
    Action getAction();

    /**
     * 获取完成交易的对象
     *
     * @return
     */
    List<DealItem<?>> getDealItemList();

}
