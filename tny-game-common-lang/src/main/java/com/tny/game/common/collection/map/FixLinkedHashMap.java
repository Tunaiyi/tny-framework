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
import java.util.Map;

public class FixLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    /**
     *
     */
    private static final long serialVersionUID = 7140688366154457969L;

    private int maxSize = 10;

    public FixLinkedHashMap(int maxSize) {
        super();
        // 规格『非正上限构造失败』：非正上限会产出 put 即自驱逐的行为未定义实例，构造显式拒绝
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive, but was: " + maxSize);
        }
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 原 >= 使插入后即刻自我驱逐、实际容量恒为 maxSize-1（maxSize=1 放进即空）
        return this.size() > this.maxSize;
    }

    public static void main(String[] args) {
        FixLinkedHashMap<Integer, String> map = new FixLinkedHashMap<>(5);
        for (int index = 0; index < 10; index++) {
            map.put(index, "第" + index + "个");
            System.out.println(map);
        }
    }

}
