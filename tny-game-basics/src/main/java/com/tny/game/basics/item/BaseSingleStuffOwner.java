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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象事物拥有者对象
 *
 * @param <IM>
 * @param <S>
 * @author KGTny
 */
public abstract class BaseSingleStuffOwner<IM extends ItemModel, SM extends StuffModel, S extends Stuff<? extends SM>>
        extends BaseStuffOwner<IM, SM, S> implements StuffOwner<IM, S> {

    /**
     * Id 索引计数器
     */
    private AtomicInteger idIndexCounter;

    protected BaseSingleStuffOwner() {
    }

    protected BaseSingleStuffOwner(long playerId, IM model) {
        super(playerId, model);
    }

    @Override
    public S getItemById(long id) {
        return this.stuffMap().get(id);
    }

    @Override
    public S getItemByModelId(int modelId) {
        return this.stuffMap().get((long) modelId);
    }

    protected abstract Map<Long, S> stuffMap();

    protected long createStuffId(SM model) {
        return model.getItemType().itemIdOf(this.idIndexCounter.getAndIncrement());
    }

    protected Collection<S> getAllStuffs() {
        return Collections.unmodifiableCollection(this.stuffMap().values());
    }

    public long getIdIndexCreator() {
        return this.idIndexCounter.get();
    }

    @Override
    protected void setStuffs(Collection<S> stuffs) {
        Map<Long, S> stuffMap = this.stuffMap();
        for (S stuff : stuffs) {
            stuffMap.put(stuff.getId(), stuff);
        }
    }

    protected BaseSingleStuffOwner<IM, SM, S> setIdIndexCounter(AtomicInteger idIndexCounter) {
        this.idIndexCounter = idIndexCounter;
        return this;
    }

    protected BaseSingleStuffOwner<IM, SM, S> setIdIndexCounter(int idIndexCounter) {
        this.idIndexCounter = new AtomicInteger(idIndexCounter);
        return this;
    }

}
