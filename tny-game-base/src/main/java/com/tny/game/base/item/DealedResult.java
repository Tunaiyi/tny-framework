package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;

import java.util.List;
import java.util.Set;

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

    /**
     * 获取改变的owner
     *
     * @return
     */
    public Set<Owner<?, ?>> getChangeOwnerSet();

    /**
     * 获取改变的item
     *
     * @return
     */
    public Set<Stuff<?>> getChangeStuffSet();

}
