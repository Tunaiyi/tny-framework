package com.tny.game.protoex.annotations;

import java.lang.annotation.*;

/**
 * ProtoEx Map字段中Key和Value编码方式
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExEntry {

    /**
     * key编码方式配置 默认为显式发送ProtoExID
     *
     * @return
     */
    ProtoExConf key() default @ProtoExConf(typeEncode = TypeEncode.EXPLICIT);

    /**
     * value编码方式配置 默认为显式发送ProtoExID
     *
     * @return
     */
    ProtoExConf value() default @ProtoExConf(typeEncode = TypeEncode.EXPLICIT);

}
