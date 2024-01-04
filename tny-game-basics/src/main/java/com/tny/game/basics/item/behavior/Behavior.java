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
