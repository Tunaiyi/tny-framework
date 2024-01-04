/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog;

public abstract class StuffSettleLog {

    /**
     * @return 物品ItemID
     */
    public abstract int getItemId();

    /**
     * @return 存量
     */
    public abstract long getNumber();

    /**
     * @return 获得数量
     */
    public abstract long getReceiveNum();

    /**
     * @return 消耗数量
     */
    public abstract long getConsumeNum();

    /**
     * 获得当前物品 alter 数量
     *
     * @param number 存量
     * @param alter  获得量
     */
    protected abstract void receive(long number, long alter);

    /**
     * 消耗当前物品 alter 数量
     *
     * @param number 存量
     * @param alter  消耗量
     */
    protected abstract void consume(long number, long alter);

}
