/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-20 21:58
 */
public class PID<M> {

    private static final Map<Integer, PID<?>> PID_MAP = new ConcurrentHashMap<>();

    private int pid;

    public int getPid() {
        return this.pid;
    }

    private PID(int pid) {
        this.pid = pid;
    }

    public static <T> PID<T> create(int pid) {
        PID<T> pidObject = new PID<>(pid);
        PID<?> old = PID_MAP.putIfAbsent(pidObject.pid, pidObject);
        if (old != null) {
            throw new IllegalArgumentException();
        }
        return pidObject;
    }

}
