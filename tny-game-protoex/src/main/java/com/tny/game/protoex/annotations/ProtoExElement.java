package com.tny.game.protoex.annotations;

import java.lang.annotation.*;

/**
 * ProtoEx Repeat字段中元素编码方式
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExElement {

    /**
     * Repeat字段中元素编码方式 默认为显式发送ProtoExID
     *
     * @return
     */
    ProtoExConf value() default @ProtoExConf(typeEncode = TypeEncode.EXPLICIT);

}
