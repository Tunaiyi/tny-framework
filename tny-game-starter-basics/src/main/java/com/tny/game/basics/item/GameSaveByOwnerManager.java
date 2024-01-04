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

/**
 * 存储当前存储的Item存储在一个Storage上的Manager
 *
 * @author KGTny
 */
public abstract class GameSaveByOwnerManager<S extends Stuff<?>, O extends BaseStuffOwner<?, ?, S>>
        extends GameSaveByHostManager<S, O, GameStuffOwnerManager<O>> {

    protected GameSaveByOwnerManager(Class<? extends S> entityClass) {
        super(entityClass);
    }

    @Override
    protected O findHost(long playerId, long id) {
        GameStuffOwnerManager<O> manager = manager();
        return manager.getOwner(playerId);
    }

    @Override
    protected O itemToHost(S item) {
        GameStuffOwnerManager<O> manager = manager();
        return manager.getOwner(item.getPlayerId());
    }

    @Override
    protected S hostToItem(O host, long id) {
        return host.getItemById(id);
    }

}
