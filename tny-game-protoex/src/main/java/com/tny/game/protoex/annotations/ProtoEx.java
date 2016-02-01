package com.tny.game.protoex.annotations;

import java.lang.annotation.*;

/**
 * 将类标记为自定义ProtoEx类型
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoEx {

    /**
     * 自定义ProtoEx类型ID 取值范围 1 - 536870911
     *
     * @return
     */
    int value();

}