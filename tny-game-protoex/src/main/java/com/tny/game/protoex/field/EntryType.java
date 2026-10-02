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

package com.tny.game.protoex.field;

import com.tny.game.protoex.*;

/**
 * map键值对类型
 *
 * @author KGTny
 */
public enum EntryType {

    /**
     * 键
     */
    KEY(1),

    /**
     * 值
     */
    VALUE(2);

    private final int index;

    private EntryType(int index) {
        this.index = index;
    }

    public int getFieldIndex() {
        return this.index;
    }

    public boolean isType(Tag tag) {
        return tag.getFieldNumber() == this.index;
    }

}
