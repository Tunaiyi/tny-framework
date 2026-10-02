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
        return (B) this;
    }

    @Override
    public O createItem() {
        O item = super.createItem();
        item.setIdIndexCounter(idIndexCounter);
        return item;
    }

}
