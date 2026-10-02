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

package com.tny.game.common.context;

public class AttrEntry<T> {

    protected AttrKey<? extends T> key;

    protected T value;

    protected AttrEntry(AttrKey<? extends T> key, T value) {
        this.key = key;
        this.value = value;
    }

    public AttrKey<? extends T> getKey() {
        return this.key;
    }

    public T getValue() {
        return this.value;
    }

}
