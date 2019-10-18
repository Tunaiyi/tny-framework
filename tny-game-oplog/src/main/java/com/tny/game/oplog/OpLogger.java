package com.tny.game.oplog;

import com.tny.game.base.item.Identifier;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.behavior.Action;

public interface OpLogger {

    /**
     * 提交日志
     */
    void submit();

    /**
     * @param item   记录Item对象
     * @param action 记录原因(操作Action)
     * @param oldNum 改变前的值
     * @param alter  改变数量
     * @param newNum 当前值
     * @return 返回Logger
     */
    OpLogger logReceive(Item<?> item, Action action, long oldNum, long alter, long newNum);

    /**
     * @param playerID 玩家ID
     * @param id       Item 的ID
     * @param model    记录Item模型
     * @param action   记录原因(操作Action)
     * @param oldNum   改变前的值
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger logReceive(long playerID, long id, ItemModel model, Action action, long oldNum, long alter, long newNum);

    /**
     * @param playerID 玩家ID
     * @param id       Item 的ID
     * @param itemID   记录ItemItemID 记录Item对象
     * @param action   记录原因(操作Action)
     * @param oldNum   改变前的值
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger logReceive(long playerID, long id, int itemID, Action action, long oldNum, long alter, long newNum);

    /**
     * @param item   记录Item对象
     * @param action 记录原因(操作Action)
     * @param oldNum 改变前的值
     * @param alter  改变数量
     * @param newNum 当前值
     * @return 返回Logger
     */
    OpLogger logConsume(Item<?> item, Action action, long oldNum, long alter, long newNum);

    /**
     * @param playerID 玩家ID
     * @param id       Item 的ID
     * @param model    记录Item模型
     * @param action   记录原因(操作Action)
     * @param oldNum   改变前的值
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger logConsume(long playerID, long id, ItemModel model, Action action, long oldNum, long alter, long newNum);

    /**
     * @param playerID 玩家ID
     * @param id       Item 的ID
     * @param itemID   记录ItemItemID 记录Item对象
     * @param action   记录原因(操作Action)
     * @param oldNum   改变前的值
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger logConsume(long playerID, long id, int itemID, Action action, long oldNum, long alter, long newNum);

    /**
     * Item获得结算
     *
     * @param item   记录Item对象
     * @param alter  改变数量
     * @param newNum 当前值
     * @return 返回Logger
     */
    OpLogger settleReceive(Item<?> item, long alter, long newNum);

    /**
     * Item获得结算
     *
     * @param playerID 玩家ID
     * @param model    记录Item模型
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger settleReceive(long playerID, ItemModel model, long alter, long newNum);

    /**
     * Item获得结算
     *
     * @param playerID 玩家ID
     * @param itemID   记录ItemItemID 记录Item对象
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger settleReceive(long playerID, int itemID, long alter, long newNum);


    /**
     * Item消耗结算
     *
     * @param item   记录Item对象
     * @param alter  改变数量
     * @param newNum 当前值
     * @return 返回Logger
     */
    OpLogger settleConsume(Item<?> item, long alter, long newNum);

    /**
     * Item消耗结算
     *
     * @param playerID 玩家ID
     * @param model    记录Item模型
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger settleConsume(long playerID, ItemModel model, long alter, long newNum);

    /**
     * Item消耗结算
     *
     * @param playerID 玩家ID
     * @param itemID   记录ItemItemID 记录Item对象
     * @param alter    改变数量
     * @param newNum   当前值
     * @return 返回Logger
     */
    OpLogger settleConsume(long playerID, int itemID, long alter, long newNum);

    /**
     * 记录快照
     *
     * @param item   记录Item对象
     * @param action 记录原因(操作Action)
     * @param types  快照器类型
     * @return 返回Logger
     */
    OpLogger logSnapshotByType(Identifier item, Action action, SnapperType... types);

    /**
     * 记录快照
     *
     * @param item         记录Item对象
     * @param action       记录原因(操作Action)
     * @param snapperTypes 快照器Class
     * @return 返回Logger
     */
    OpLogger logSnapshotByClass(Identifier item, Action action, Class<? extends Snapper>... snapperTypes);

    /**
     * 记录快照
     *
     * @param item   记录Item对象
     * @param action 记录原因(操作Action)
     * @return 返回Logger
     */
    OpLogger logSnapshot(Identifier item, Action action);

    /**
     * @return 是否开启记录 返回Logger
     */
    boolean isLogged();

}