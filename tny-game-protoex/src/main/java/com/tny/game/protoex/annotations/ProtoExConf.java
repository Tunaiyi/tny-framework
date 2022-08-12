/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex.annotations;

import com.tny.game.protoex.field.*;

import java.lang.annotation.*;

/**
 * ProtoEx字段编码配置
 *
 * @author KGTny
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExConf {

    /**
     * int long short 字段的编码方式
     * 默认为 FieldFormat.DEFAULT
     *
     * @return
     */
    FieldFormat format() default FieldFormat.DEFAULT;

    /**
     * 字段类型的ProtoExID编码方式 默认为TypeEncode.DEFAULT
     *
     * @return
     */
    TypeEncode typeEncode() default TypeEncode.DEFAULT;

    /**
     * 使用指定类型
     *
     * @return
     */
    Class<?> use() default Void.class;

}
