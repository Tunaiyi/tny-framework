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

/**
 * @author KGTny
 * @ClassName: CountableStuffStorage
 * @Description: 物品项拥有者
 * @date 2011-11-3 上午9:50:52
 * <p>
 * <p>
 * <br>
 */
public interface MultipleStuffOwner<IM extends ItemModel, SM extends MultipleStuffModel, S extends Stuff<? extends SM>>
        extends StuffOwner<IM, S> {

    /**
     * 检测是否满了
     *
     * @param model  测试物品模型
     * @param number 添加数量
     * @return 溢出返回 true 否则返回 false
     */
    boolean isOverage(SM model, AlterType type, Number number);

    /**
     * 检测是否足够
     *
     * @param model  测试物品模型
     * @param number 扣除数量
     * @return 不足返回 true 否则返回 false
     */
    boolean isLack(SM model, AlterType type, Number number);

}