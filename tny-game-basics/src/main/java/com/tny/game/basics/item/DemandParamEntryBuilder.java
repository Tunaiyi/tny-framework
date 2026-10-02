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

import com.tny.game.basics.item.behavior.*;

import java.util.*;

public class DemandParamEntryBuilder {

    private List<DemandParamEntry<?>> entryList = new ArrayList<DemandParamEntry<?>>();

    private DemandParamEntryBuilder() {
    }

    public static final DemandParamEntryBuilder newBuilder() {
        return new DemandParamEntryBuilder();
    }

    public <T> DemandParamEntryBuilder put(DemandParam param, T value) {
        this.entryList.add(new DemandParamEntry<T>(param, value));
        return this;
    }

    public DemandParamEntry<?>[] buildArray() {
        return entryList.toArray(new DemandParamEntry<?>[entryList.size()]);
    }

    public Collection<DemandParamEntry<?>> buildEntries() {
        return Collections.unmodifiableCollection(this.entryList);
    }

}
