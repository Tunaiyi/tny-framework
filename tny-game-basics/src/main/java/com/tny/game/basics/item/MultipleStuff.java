/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

public interface MultipleStuff<IM extends MultipleStuffModel, N extends Number> extends Stuff<IM> {

    int UNLIMITED = -1;

    /**
     * 是否有上限 <br>
     *
     * @return
     */
    boolean isNumberLimit();

    /**
     * 获取上限 <br>
     *
     * @return
     */
    N getNumberLimit();

    /**
     * 物品数量 <br>
     *
     * @return
     */
    N getNumber();

    /**
     * 判断是否足够
     *
     * @param costNum
     * @return
     */
    boolean tryEnough(long costNum);

    /**
     * 是否超出资源上限
     *
     * @return
     */
    boolean tryFull(long costNum);

    /**
     * 是否超过上线
     *
     * @return
     */
    boolean isFull();

}