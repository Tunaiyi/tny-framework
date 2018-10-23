package com.tny.game.protoex.annotations;

import com.tny.game.protoex.field.FieldFormat;

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
