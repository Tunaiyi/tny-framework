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
