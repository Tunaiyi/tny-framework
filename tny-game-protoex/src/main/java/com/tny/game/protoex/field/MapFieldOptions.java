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

package com.tny.game.protoex.field;

/**
 * Map键值对编码方式配置
 *
 * @param <M>
 * @author KGTny
 */
public interface MapFieldOptions<M> extends FieldOptions<M> {

    /**
     * key编码方式
     *
     * @return
     */
    public FieldOptions<?> getKeyOptions();

    /**
     * value编码方式
     *
     * @return
     */
    public FieldOptions<?> getValueOptions();

}
