/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
