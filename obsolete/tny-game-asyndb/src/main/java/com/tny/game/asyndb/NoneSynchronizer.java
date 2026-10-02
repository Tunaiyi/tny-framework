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

public abstract class NoneSynchronizer<O> implements Synchronizer<O> {

    @Override
    public boolean insert(O object) {
        return true;
    }

    @Override
    public Collection<O> insert(Collection<O> objects) {
        return Collections.emptyList();
    }

    @Override
    public boolean update(O object) {
        return true;
    }

    @Override
    public Collection<O> update(Collection<O> objects) {
        return Collections.emptyList();
    }

    @Override
    public boolean delete(O object) {
        return true;
    }

    @Override
    public Collection<O> delete(Collection<O> objects) {
        return Collections.emptyList();
    }

    @Override
    public boolean save(O object) {
        return true;
    }

    @Override
    public Collection<O> save(Collection<O> objects) {
        return Collections.emptyList();
    }

}
