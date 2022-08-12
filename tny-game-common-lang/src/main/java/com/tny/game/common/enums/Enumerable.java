/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.enums;

import com.tny.game.common.utils.*;

/**
 * Created by Kun Yang on 16/1/29.
 */

public interface Enumerable<ID> {

    ID getId();

    String name();

    default void checkEnum() {
        ID id = getId();
        ConfigChecker.check(this.getClass(), id, "{}-[{}-ID:{}]发生重复", this.getClass(), this, id);
    }

}
