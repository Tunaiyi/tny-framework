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

package com.tny.game.protoex.annotations;

import java.lang.annotation.*;

/**
 * ProtoEx编码配置Repeat(Collection)类型字段是否打包
 * 默认是打包
 * <p>
 * Packed:
 * element无长度:
 * [TypeTag][FieldTag][length][elementOption][element_1][element_2][element_3]...[element_n]
 * element有长度:
 * Ï 			[TypeTag][FieldTag][length][elementOption][elLength_1][element_2][elLength_2][element_1][elLength_3][element_3]..
 * .[elLength_n][element_n]
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Packed {

    /**
     * 是否打包
     * true : 打包
     * false : 不打包
     *
     * @return
     */
    boolean value() default true;

}