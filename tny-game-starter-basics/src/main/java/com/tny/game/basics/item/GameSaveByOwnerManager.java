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
