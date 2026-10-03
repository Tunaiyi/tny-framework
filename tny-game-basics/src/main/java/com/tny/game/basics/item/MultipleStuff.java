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