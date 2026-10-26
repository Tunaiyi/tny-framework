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
package com.tny.game.cache.simple;

import com.tny.game.cache.*;

public class SimpleCasItem<T> implements CasItem<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String key;

    private T data;

    private long version;

    public SimpleCasItem(String key, T data, long version) {
        this.key = key;
        this.data = data;
        this.version = version;
    }

    public SimpleCasItem(CasItem<?> mItem, T newData) {
        this.init(mItem, newData);
    }

    private void init(CasItem<?> mItem, T newValue) {
        this.key = mItem.getKey();
        this.data = newValue;
        this.version = mItem.getVersion();
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public T getData() {
        return this.data;
    }

    protected void update() {
        this.version++;
    }

    public static void main(String[] args) {
        System.out.println("LOCK_HAED_ssss".substring("LOCK_HAED_".length()));
    }

}
