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

package com.tny.game.net.command.dispatcher;

import java.lang.reflect.Method;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 参数索引创建器
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/2 02:15
 **/
public class ParamIndexCreator {

    private final Method method;

    private final SortedSet<Integer> useIndexes = new TreeSet<>();

    private int step = 0;

    public ParamIndexCreator(Method method) {
        this.method = method;
    }

    public int peek() {
        int index;
        do {
            index = step;
            step++;
        } while (!useIndexes.add(index));
        return index;
    }

    public int use(int index) {
        if (this.useIndexes.add(index)) {
            return index;
        } else {
            throw new IllegalArgumentException(format("{} 反复 {} 参数出现重复", method, index));
        }
    }

}
