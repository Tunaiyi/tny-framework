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

import java.util.Collection;

/**
 * Storage总管理器
 *
 * @author KGTny
 */
public interface StuffOwnerExplorer {

    <O extends StuffOwner<?, ?>> O getOwner(long playerId, ItemType ownerType);

    boolean insertOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> insertOwner(Collection<O> storageCollection);

    boolean updateOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> updateOwner(Collection<O> storageCollection);

    boolean saveOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> saveOwner(Collection<O> storageCollection);

    void deleteOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> void deleteOwner(Collection<O> storageCollection);

}