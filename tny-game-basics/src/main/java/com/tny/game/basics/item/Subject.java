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

package com.tny.game.basics.item;

import com.tny.game.common.tag.*;

import java.util.Set;

public interface Subject<M extends Model> extends Any, Taggable {

    /**
     * @return 获取对象别名
     */
    String getAlias();

    /**
     * @return 获取该事物模型ID
     */
    int getModelId();

    /**
     * @return 获取该事物对象的模型
     */
    M getModel();

    @Override
    default Set<Object> tags() {
        return this.getModel().tags();
    }

}
