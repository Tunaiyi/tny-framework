/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
