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

package com.tny.game.oplog;

import org.slf4j.*;

import java.util.function.Supplier;

public class Alter<N> {

    public static final Logger LOGGER = LoggerFactory.getLogger(Alter.class);

    private N value;

    private N lately;

    private Supplier<N> supplier;

    public static <N> Alter<N> of(Supplier<N> supplier) {
        return new Alter<N>(supplier);
    }

    public static <N> Alter<N> of(N value) {
        return new Alter<N>(value);
    }

    public static <N> Alter<N> of(N value, N lately) {
        return new Alter<N>(value, lately);
    }

    private Alter(N value) {
        this.value = value;
    }

    private Alter(N value, N lately) {
        this.value = value;
        this.lately = lately;
    }

    private Alter(Supplier<N> supplier) {
        this.supplier = supplier;
        this.value = supplier.get();
    }

    public N getValue() {
        return this.value;
    }

    public N getLately() {
        return this.lately;
    }

    public void update() {
        if (this.supplier == null) {
            LOGGER.warn("alter supplier is null", new NullPointerException());
        }
        this.update(this.supplier.get());
    }

    public void update(N alter) {
        if (alter == null) {
            return;
        }
        if (this.lately != null) {
            this.lately = alter;
        } else if (this.value == null || !this.value.equals(alter)) {
            this.lately = alter;
        }
    }

    public boolean isChange() {
        return this.lately != null;
    }

    public String toString(String defaultValue) {
        if (this.lately == null) {
            return this.value == null ? defaultValue : this.value.toString();
        } else {
            return (this.value == null ? defaultValue : this.value.toString()) + "->" + this.lately;
        }
    }

    @Override
    public String toString() {
        if (this.lately == null) {
            return this.value.toString();
        } else {
            return this.value + "->" + this.lately;
        }
    }

    public boolean isHasValue() {
        return this.value != null;
    }

    public boolean isHasWorth() {
        return this.value != null || this.lately != null;
    }

}
