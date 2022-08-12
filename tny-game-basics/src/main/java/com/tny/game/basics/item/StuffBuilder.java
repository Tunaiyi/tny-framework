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
        return (B)this;
    }

}
