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

package com.tny.game.basics.item.behavior;

import com.tny.game.basics.mould.*;
import com.tny.game.common.enums.*;

/**
 * 行为类型接口
 *
 * @author KGTny
 */
public interface Behavior extends IntEnumerable {

    /**
     * 获取模块类型
     *
     * @return
     */
    Feature getFeature();

    /**
     * 标识
     *
     * @return
     */
    @Override
    String name();

    /**
     * 描述
     *
     * @return
     */
    String getDesc();

    /**
     * 通过指定值获取对应的Action
     *
     * @param value 对应直
     * @return 对应的Action
     */
    Action forAction(Object value);

}
