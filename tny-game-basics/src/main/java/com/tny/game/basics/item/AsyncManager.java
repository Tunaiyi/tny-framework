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
import java.util.concurrent.CompletionStage;

/**
 * 管理器接口
 *
 * @param <O>
 * @author KGTny
 */
public interface AsyncManager<O> {

    CompletionStage<Boolean> save(O item);

    /**
     * @param itemCollection 保存实体列表
     * @return 返回存储失败的列表
     */
    CompletionStage<Collection<O>> save(Collection<O> itemCollection);

    CompletionStage<Boolean> update(O item);

    /**
     * @param itemCollection 保存实体列表
     * @return 返回更新失败的列表
     */
    CompletionStage<Collection<O>> update(Collection<O> itemCollection);

    CompletionStage<Boolean> insert(O item);

    /**
     * @param itemCollection 保存实体列表
     * @return 返回插入失败的列表
     */
    CompletionStage<Collection<O>> insert(Collection<O> itemCollection);

    CompletionStage<Void> delete(O item);

    CompletionStage<Void> delete(Collection<O> itemCollection);

}
