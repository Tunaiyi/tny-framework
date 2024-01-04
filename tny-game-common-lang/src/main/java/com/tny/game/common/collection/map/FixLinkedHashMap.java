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

package com.tny.game.common.collection.map;

import java.util.LinkedHashMap;

public class FixLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    /**
     *
     */
    private static final long serialVersionUID = 7140688366154457969L;

    private int maxSize = 10;

    public FixLinkedHashMap(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return this.size() >= this.maxSize;
    }

    public static void main(String[] args) {
        FixLinkedHashMap<Integer, String> map = new FixLinkedHashMap<>(5);
        for (int index = 0; index < 10; index++) {
            map.put(index, "第" + index + "个");
            System.out.println(map);
        }
    }

}
