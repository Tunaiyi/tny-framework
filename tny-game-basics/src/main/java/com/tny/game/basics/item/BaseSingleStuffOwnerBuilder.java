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
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/26 3:07 上午
 */
@SuppressWarnings("unchecked")
public abstract class BaseSingleStuffOwnerBuilder<
        IM extends ItemModel,
        SM extends StuffModel,
        S extends Stuff<? extends SM>,
        O extends BaseSingleStuffOwner<IM, ? extends SM, S>,
        B extends StuffOwnerBuilder<IM, S, O, B>>
        extends StuffOwnerBuilder<IM, S, O, B> {

    private int idIndexCounter = 0;

    public B setIdIndexCounter(int idIndexCounter) {
        this.idIndexCounter = idIndexCounter;
        return (B)this;
    }

    @Override
    public O createItem() {
        O item = super.createItem();
        item.setIdIndexCounter(idIndexCounter);
        return item;
    }

}
