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
