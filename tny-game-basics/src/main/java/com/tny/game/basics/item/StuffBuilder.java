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
 * stuff构建器
 *
 * @param <S>  stuff类型
 * @param <SM> stuffModel类型
 * @param <B>  stuffBuilder类型
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class StuffBuilder<S extends BaseItem<SM>, SM extends StuffModel, B extends StuffBuilder<S, SM, B>> extends
        ItemBuilder<S, SM, B> {

    protected int number;

    /**
     * 设置number <br>
     *
     * @param number 数量
     * @return 构建器
     */
    public B setNumber(int number) {
        this.number = number;
        return (B) this;
    }

}
