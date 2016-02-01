package com.tny.game.base.item;

import com.tny.game.base.exception.ItemResultCode;

/**
 * 尝试交易操作结果
 *
 * @author KGTny
 */
public class TryToTradeResult {

    /**
     * 玩家ID
     */
    private long playerID;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 相关事物模型
     */
    private ItemModel itemModel;

    /**
     * 现有数量
     */
    private int number;

    /**
     * 修改数量
     */
    private int alert;

    /**
     * 失败原因
     */
    private ItemResultCode resultCode;

    public TryToTradeResult(long playerID) {
        this.playerID = playerID;
        this.success = true;
    }

    public TryToTradeResult(long playerID, ItemModel itemModel, int number, int alert, ItemResultCode resultCode) {
        super();
        this.playerID = playerID;
        this.success = false;
        this.itemModel = itemModel;
        this.number = number;
        this.alert = alert;
        this.resultCode = resultCode;
    }

    public long getPlayerID() {
        return playerID;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return the itemModel
     */
    public ItemModel getItemModel() {
        return itemModel;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the alert
     */
    public int getAlert() {
        return alert;
    }

    /**
     * @return the resultCode
     */
    public ItemResultCode getResultCode() {
        return resultCode;
    }

}
