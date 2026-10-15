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
package com.tny.game.asyndb;

import java.util.*;

/**
 * @author KGTny
 * @ClassName: Persistor
 * @Description: 持久化执行器
 * @date 2011-10-8 下午4:49:23
 * <p>
 * 持久化执行器
 * <p>
 * 负责执行持久化任务<br>
 */
public interface Synchronizer<O> {

    boolean insert(O object);

    Collection<O> insert(Collection<O> objects);

    boolean update(O object);

    Collection<O> update(Collection<O> objects);

    boolean delete(O object);

    Collection<O> delete(Collection<O> objects);

    boolean save(O object);

    Collection<O> save(Collection<O> objects);

    O get(Class<? extends O> clazz, String key);

    Map<String, ? extends O> get(Class<? extends O> clazz, Collection<String> keyValues);

}
