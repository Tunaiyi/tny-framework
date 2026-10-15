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

import com.tny.game.common.enums.*;
import com.tny.game.common.result.*;

/**
 * 条件类型接口
 * <p>
 * 枚举命名规则
 * <p>
 * EQ : =
 * NE : !=
 * GE : >=
 * LE : <=
 * GR : >
 * LE : <
 *
 * @author KGTny
 */
public interface DemandType extends IntEnumerable {

    /**
     * 是否是costDemand
     *
     * @return
     */
    boolean isCost();

    /**
     * 返回错误码
     *
     * @return
     */
    ResultCode getResultCode();

}
