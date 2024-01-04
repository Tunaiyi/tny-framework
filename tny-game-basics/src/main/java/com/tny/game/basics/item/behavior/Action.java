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
 * 行为的操作类型接口
 *
 * @author KGTny
 */
public interface Action extends IntEnumerable {

    /**
     * 所属行为
     *
     * @return
     */
    Behavior getBehavior();

    /**
     * 所属系统
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

}
