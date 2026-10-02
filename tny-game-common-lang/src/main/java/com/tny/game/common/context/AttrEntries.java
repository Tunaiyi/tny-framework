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

import java.util.*;

public class AttrEntries {

    private List<AttrEntry<?>> entryList = new ArrayList<>();

    private AttrEntries() {
    }

    public static final <T> AttrEntry<T> create(AttrKey<T> key, T value) {
        return new AttrEntry<>(key, value);
    }

    public static final AttrEntries newBuilder() {
        return new AttrEntries();
    }

    public <T> AttrEntries put(AttrKey<T> key, T value) {
        this.entryList.add(new AttrEntry<>(key, value));
        return this;
    }

    public AttrEntry<?>[] build() {
        return this.entryList.toArray(new AttrEntry<?>[this.entryList.size()]);
    }

    public Collection<AttrEntry<?>> buildEntries() {
        return Collections.unmodifiableCollection(this.entryList);
    }

}
