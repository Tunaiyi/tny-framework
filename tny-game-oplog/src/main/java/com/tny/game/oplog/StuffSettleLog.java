/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
