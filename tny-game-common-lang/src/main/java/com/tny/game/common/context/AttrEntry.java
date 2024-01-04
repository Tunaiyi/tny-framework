/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
