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

package com.tny.game.common.collection;

import com.tny.game.common.utils.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.*;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class CollectorsAide {

    public static <T, K> Collector<T, ?, Map<K, T>> toMap(Function<? super T, ? extends K> keyMapper) {
        // 重复键 last-win（与单条注册覆盖语义一致；原实现同类实例重复注册直接 IllegalStateException）
        return Collectors.toMap(keyMapper, ObjectAide::self, (existing, replacement) -> replacement);
    }

}
